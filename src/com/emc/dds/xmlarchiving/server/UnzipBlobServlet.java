/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.emc.dds.xmlarchiving.server.operation.OperationsUtil;
import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.persistence.data.ByteArrayData;
import com.emc.documentum.xml.dds.persistence.data.InputStreamData;
import com.emc.documentum.xml.dds.persistence.data.ObjectData;
import com.emc.documentum.xml.dds.persistence.data.StringData;
import com.emc.documentum.xml.dds.service.DDSServiceType;
import com.emc.documentum.xml.dds.servlet.ApplicationStarter;
import com.emc.documentum.xml.dds.uri.DDSURI;
import com.emc.documentum.xml.dds.uri.URITarget;
import com.emc.documentum.xml.dds.uri.exception.DDSURIException;
import com.emc.documentum.xml.dds.user.TokenService;
import com.emc.documentum.xml.dds.user.TokenToTokenMapping;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.user.UserService;
import com.emc.documentum.xml.dds.user.UserToken;

public class UnzipBlobServlet extends HttpServlet {

  /**
   * Name of the request attribute holding the DDS URI of the blob.
   */
  public static final String DDS_URI_ATTRIBUTE = "uri";

  public UnzipBlobServlet() {
    super();
  }

  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
    doBlob(request, response);
  }

  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
    doBlob(request, response);
  }

  private void doBlob(final HttpServletRequest request, final HttpServletResponse response) {
    try {
      String uri = request.getParameter(DDS_URI_ATTRIBUTE);
      if (uri != null && !uri.equals("")) {
        Application application = DDS.getApplication();
        User user = getUser(request, application);
        try {
          URITarget uriTarget =
              application.getDefaultURIResolver().resolveURI(DDSURI.parseURI(uri), user);
          StoreChild storeChild = uriTarget.getStoreChild();
          if (storeChild != null) {
            if (storeChild.isContainer()) {
              RetrieveOperation retrieveOperation =
                  new RetrieveOperation((Container)storeChild, null);
              Data<?> data = application.execute(user, retrieveOperation);
              OutputStream os = response.getOutputStream();
              if (data instanceof InputStreamData) {
                String fileName = storeChild.getName();
                String mimetype = getMimeType(fileName);
                if (mimetype != null && mimetype.equals("application/zip")) {
                  ZipInputStream zis = new ZipInputStream(((InputStreamData)data).content());
                  try {
                    for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry =
                        zis.getNextEntry()) {
                      if (!zipEntry.isDirectory()) {
                        final String path = zipEntry.getName();
                        mimetype = getMimeType(path);
                        InputStream is = new NonClosingInputStreamWrapper(zis);
                        setResponse(response, is, mimetype, fileName);
                      }
                    }
                  } finally {
                    zis.close();
                  }
                } else {
                  setResponse(response, ((InputStreamData)data).content(), mimetype, fileName);
                }
              } else if (data instanceof ByteArrayData) {
                os.write(((ByteArrayData)data).content());
              } else if (data instanceof StringData) {
                os.write(((StringData)data).content().getBytes(StandardCharsets.UTF_8));
              } else if (data instanceof ObjectData) {
                os.write(((ObjectData)data).content().toString().getBytes(StandardCharsets.UTF_8));
              }
            }
          }
        } catch (DDSURIException e) {
            response.sendError(404, "Resource: Invalid URI");
        }
        catch (Exception e) {
          response.sendError(404, "Resource: " + uri + " not found");
        }
      }
    } catch (IOException ioe) {
      LogCenter.exception(this, ioe);
    }
  }

  private String getMimeType(String fileName) throws IOException {
    String extension = OperationsUtil.getExtension(fileName);
    if (extension == null || extension.equals("") || extension.equalsIgnoreCase("att")) {
      return null;
    }
    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
    String mimetype = mimeTypesMap.getContentType(fileName);
    if (mimetype.equals("application/octet-stream")) {
      // nothing found. Try to load the application mime types
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("mime.types");
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }finally {
            is.close();
        }
        mimeTypesMap.addMimeTypes(sb.toString());
        mimetype = mimeTypesMap.getContentType(fileName);
    }
    return mimetype;
  }

  private void setResponse(HttpServletResponse response, InputStream is, String mimetype,
      String name) throws Exception {
    OutputStream os = response.getOutputStream();
    if (mimetype != null) {
      response.setContentType(mimetype);
    }
    response.setHeader("Cache-Control", "max-age=0");
    response.setHeader("Content-Disposition", "inline; filename=\"" + name);
    if (is != null) {
      int c;
      while ((c = is.read()) != -1) {
        os.write(c);
      }
      is.close();
    }
  }

  private User getUser(final HttpServletRequest request, final Application application) {
    User user = null;
    if (application != null) {
      UserService userManager =
          (UserService)application.getServiceManager().getService(DDSServiceType.USER);
      if (userManager == null) {
        user = application.getApplicationUser();
      } else {
        TokenService tokenManager =
            (TokenService)application.getServiceManager().getService(DDSServiceType.TOKEN);
        if (tokenManager != null) {
          TokenToTokenMapping tokenToTokenMapping = tokenManager.getTokenToTokenMapping();
          HttpSession session = request.getSession();
          if (session != null) {
            String userName = null;
            UserToken userToken = tokenToTokenMapping.getUserToken(session.getId());
            if (userToken == null) {
              userName = (String)getFromScope(request, ApplicationStarter.USER_ID_ATTRIBUTE);
            } else {
              userName = userToken.getUserId();
            }
            if (userName != null) {
              user = userManager.getUser(userName);
            }
          }
        }
      }
    }
    return user;
  }

  private Object getFromScope(HttpServletRequest request, String key) {
    Object result = request.getAttribute(key);
    if (result != null) {
      return result;
    }
    HttpSession session = request.getSession();
    if (session != null) {
      result = session.getAttribute(key);
      if (result != null) {
        return result;
      }
    }
    ServletConfig servletConfig = getServletConfig();
    if (servletConfig != null) {
      result = servletConfig.getInitParameter(key);
      if (result != null) {
        return result;
      }
    }
    ServletContext servletContext = getServletContext();
    if (servletContext != null) {
      result = servletContext.getInitParameter(key);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private static class NonClosingInputStreamWrapper extends InputStream {

    // VT - we are using this wrapper input stream to prevent closing the main
    // ZIP input stream

    private final InputStream is;

    public NonClosingInputStreamWrapper(final InputStream is) {
      this.is = is;
    }

    @Override
    public int available() throws IOException {
      return is.available();
    }

    @Override
    public void close() throws IOException {
      // nedelej nic
    }

    @Override
    public synchronized void mark(final int readlimit) {
      is.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
      return is.markSupported();
    }

    @Override
    public int read() throws IOException {
      return is.read();
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
      return is.read(b, off, len);
    }

    @Override
    public int read(final byte[] b) throws IOException {
      return is.read(b);
    }

    @Override
    public synchronized void reset() throws IOException {
      is.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
      return is.skip(n);
    }

  }

}
