/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;

import org.apache.tools.ant.taskdefs.Expand;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import com.emc.dds.xmlarchiving.server.query.AttachLibraries;
import com.emc.dds.xmlarchiving.server.query.CreateDetachableLibrary;
import com.emc.dds.xmlarchiving.server.query.DetachLibraries;
import com.emc.dds.xmlarchiving.server.query.ReadFromCentera;
import com.emc.dds.xmlarchiving.server.query.WriteToCentera;
import com.emc.documentum.xml.dds.DDS;
import com.emc.documentum.xml.dds.internal.DDSConstants;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSMetadata;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSURIExtensionFunction;
import com.emc.documentum.xml.dds.xquery.xdb.internal.DDSXQueryUtils;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.index.interfaces.XhiveIndexAdderIf;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.query.interfaces.XhivePreparedQueryIf;
import com.xhive.query.interfaces.XhiveXQueryCompilerIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;

public final class OperationsUtil {

  public static final QName XQUERY_FUNCTION_DETACH_LIBRARIES = new QName(
      DDSConstants.DDS_NAMESPACE_URI, "detach-libraries");
  public static final QName XQUERY_FUNCTION_ATTACH_LIBRARIES = new QName(
      DDSConstants.DDS_NAMESPACE_URI, "attach-libraries");
  public static final QName XQUERY_FUNCTION_READ_FROM_CENTERA = new QName(
      DDSConstants.DDS_NAMESPACE_URI, "read-from-centera");
  public static final QName XQUERY_FUNCTION_WRITE_TO_CENTERA = new QName(
      DDSConstants.DDS_NAMESPACE_URI, "write-to-centera");
  public static final QName XQUERY_FUNCTION_CREATE_DETACHABLE_LIBRARY = new QName(
      DDSConstants.DDS_NAMESPACE_URI, "create-detachable-library");

  protected static String getXQuery(XhiveLibraryIf root, String applicationName, String type,
      String xqueryName) {
    XhiveDocumentIf document =
        (XhiveDocumentIf)root.getByPath(getQueryPath(applicationName, type, xqueryName));
    if (document != null) {
      return getElementValue(document.getDocumentElement(), "expression");
    }
    return null;
  }

  public static Element addElement(Document document, Element parent, String namespace,
      String localName, String value) {
    Element element = document.createElementNS(namespace, localName);
    parent.appendChild(element);
    Text text = document.createTextNode(value);
    element.appendChild(text);
    return element;
  }

  public static Element getElement(Element parent, String localName) {
    NodeList elements = parent.getElementsByTagNameNS(DDSConstants.DDS_NAMESPACE_URI, localName);
    if (elements.getLength() > 0) {
      return (Element)elements.item(0);
    }
    return null;
  }

  public static String getElementValue(Element parent, String uri, String localName) {
    NodeList elements = parent.getElementsByTagNameNS(uri, localName);
    if (elements.getLength() > 0) {
      return elements.item(0).getTextContent();
    }
    return null;
  }

  public static String getElementValue(Element parent, String localName) {
    NodeList elements = parent.getElementsByTagName(localName);
    if (elements.getLength() > 0) {
      return elements.item(0).getTextContent();
    }
    return null;
  }

  public static String getTextContent(Element element) {
    StringBuffer sb = new StringBuffer("");
    Node child = element.getFirstChild();
    while (child != null) {
      if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
        sb.append(child.getNodeValue());
      }
      child = child.getNextSibling();
    }
    return sb.toString();
  }

  protected static List<String> getAllTypes(XhiveLibraryIf rootLib, String applicationName) {
    List<String> result = new ArrayList<String>();
    XhiveLibraryIf xqueryLib =
        (XhiveLibraryIf)rootLib.getByPath("/APPLICATIONS/" + applicationName
            + "/resources/xqueries");
    if (xqueryLib != null) {
      Node child = xqueryLib.getFirstChild();
      while (child != null) {
        if (child instanceof XhiveLibraryIf) {
          result.add(((XhiveLibraryIf)child).getName());
        }
        child = child.getNextSibling();
      }
    }
    return result;
  }

  protected static String getQueryPath(String applicationName, String type, String xqueryName) {
    StringBuffer sb = new StringBuffer();
    sb.append("/APPLICATIONS/");
    sb.append(applicationName);
    sb.append("/resources/xqueries/");
    sb.append(type);
    sb.append('/');
    sb.append(xqueryName);
    return sb.toString();
  }

  protected static String getLibraryPath(String dataset) {
    StringBuffer sb = new StringBuffer();
    sb.append("/DATA/");
    sb.append(dataset);
    sb.append("/Collection");
    return sb.toString();
  }

  /**
   * Tests whether the ZIP file is empty (i.e. contains no entries)
   * @param zipFile the ZIP file to test
   * @return <code>true</code> iff the ZIP file is empty
   * @throws java.io.IOException
   */
  protected static boolean isEmptyZipFile(File zipFile) throws IOException {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(zipFile);
      return isEmptyZipArchive(fis);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }

  /**
   * Tests whether the ZIP archive represented by <code>is</code> is empty (i.e. contains no
   * entries).
   * @param is the input stream to test
   * @return <code>true</code> iff the archive is empty
   * @throws IOException
   */
  protected static boolean isEmptyZipArchive(InputStream is) throws IOException {
    byte[] emptyZipBytes = new byte[22];
    emptyZipBytes[0] = 80; // P
    emptyZipBytes[1] = 75; // K
    emptyZipBytes[2] = 5;
    emptyZipBytes[3] = 6;
    // the rest is zeros

    byte[] bytes = new byte[22];
    is.read(bytes, 0, 22);
    return Arrays.equals(bytes, emptyZipBytes);
  }

  public static String getExtension(String fileName) {
    int index = fileName.lastIndexOf('.');
    if (index != -1) {
      return fileName.substring(index + 1);
    }
    return null;
  }

  protected static Document parseDocument(FileInputStream fis) {
    return parseDocument(fis, false, null, null);
  }

  protected static Document parseDocument(FileInputStream fis, boolean validate,
      String schemaLocation, DOMErrorHandler errorHandler) {
    DOMImplementationLS impl = new DOMImplementationImpl();
    LSParser parser =
        impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
            javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    LSInput input = impl.createLSInput();
    input.setByteStream(fis);
    DOMConfiguration domConfig = parser.getDomConfig();
    if (validate) {
      domConfig.setParameter("validate", validate);
      if (schemaLocation != null) {
        domConfig.setParameter("schema-type", javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = Thread.currentThread().getContextClassLoader().getResource(schemaLocation);
        domConfig.setParameter("schema-location", url.getPath());
      }
      if (errorHandler != null) {
        domConfig.setParameter("error-handler", errorHandler);
      }
    }
    return parser.parse(input);
  }

  protected static void serializeDocument(Document xmlDoc, FileOutputStream fos) {
    DOMImplementationLS impl = new DOMImplementationImpl();
    LSSerializer serializer = impl.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
    LSOutput output = impl.createLSOutput();
    output.setByteStream(fos);
    serializer.write(xmlDoc, output);
  }

  protected static void unzip(File zipFile, File targetDir) throws IOException {
    // First check whether the zipfile is a zipfile, the ant-task will never say this
    ZipFile testZip = new ZipFile(zipFile);
    testZip.entries();
    testZip.close();
    // Unzip uploaded file
    org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
    Expand unzip = new Expand();
    unzip.setProject(project);
    unzip.setSrc(zipFile);
    unzip.setDest(targetDir);
    unzip.setOverwrite(true);
    unzip.execute();
  }

  protected static boolean deleteFile(File file) {
    int result = doDeleteFile(file);
    if (result == 1) {
      return true;
    }
    return false;
  }

  private static int doDeleteFile(File file) {
    int result = 1;
    if (file.exists()) {
      if (file.isDirectory()) {
        File[] contents = file.listFiles();
        for (File content : contents) {
          result = doDeleteFile(content);
          if (result == -1) {
            return -1;
          }
        }
        boolean deleted = file.delete();
        if (!deleted) {
          result = 0;
          file.deleteOnExit();
        }
      } else {
        boolean deleted = file.delete();
        if (!deleted) {
          result = 0;
          file.deleteOnExit();
        }
      }
    }
    return result;
  }

  public static Set<String> stringAsSet(String string) {
    LinkedHashSet<String> result = new LinkedHashSet<String>();
    for (StringTokenizer tok = new StringTokenizer(string, ","); tok.hasMoreTokens();) {
      String elem = tok.nextToken();
      result.add(elem.trim());
    }
    return result;
  }

  public static XhiveLibraryIf createRetentionLibrary(XhiveLibraryIf parentLibrary,
      String retentionLibraryName) {
    XhiveDatabaseIf database = parentLibrary.getDatabase();
    String retentionLibraryPath = parentLibrary.getFullPath() + "/" + retentionLibraryName;
    String segmentId = getNewSegmentID(database, getDefaultSegmentId(retentionLibraryPath));
    return createRetentionLibrary(parentLibrary, retentionLibraryName, segmentId);
  }

  public static XhiveLibraryIf createRetentionLibrary(XhiveLibraryIf parentLibrary,
      String retentionLibraryName, String segmentId) {
    XhiveDatabaseIf database = parentLibrary.getDatabase();
    database.createSegment(segmentId, null, 0);
    XhiveLibraryIf retentionLibrary =
        parentLibrary.createLibrary(XhiveLibraryIf.CONCURRENT_LIBRARY
            | XhiveLibraryIf.DETACHABLE_LIBRARY, segmentId);
    retentionLibrary.setName(retentionLibraryName);
    parentLibrary.appendChild(retentionLibrary);
    return retentionLibrary;
  }

  protected static void addIndexes(XhiveIndexAdderIf indexAdder, XhiveLibraryIf sourceLib,
      Set<String> excludeIndexNames) {
    for (XhiveIndexIf index : sourceLib.getIndexList().iterator()) {
      if (excludeIndexNames == null || !excludeIndexNames.contains(index.getName())) {
        switch (index.getType()) {
          case XhiveIndexIf.FULL_TEXT_INDEX:
            indexAdder.addFullTextIndex(index.getName(), index.getElementURI(),
                index.getElementName(), index.getAttributeURI(), index.getAttributeName(),
                index.getAnalyzerClassName(), index.getOptions());
            break;
          case XhiveIndexIf.VALUE_INDEX:
            indexAdder.addValueIndex(index.getName(), index.getElementURI(),
                index.getElementName(), index.getAttributeURI(), index.getAttributeName(),
                index.getOptions());
            break;
          case XhiveIndexIf.PATH_VALUE_INDEX:
            indexAdder.addPathValueIndex(index.getName(), index.getPathIndexPath(),
                index.getOptions());
            break;
          case XhiveIndexIf.ELEMENT_NAME_INDEX:
            indexAdder.addElementNameIndex(index.getName(), index.getSelectedElementIndexNames(),
                index.getOptions());
            break;
        }
      }
    }
  }

  public static void addIndexes(XhiveLibraryIf targetLib, XhiveLibraryIf sourceLib,
      Set<String> excludeIndexNames) {
    XhiveIndexAdderIf indexAdder = targetLib.getIndexAdder();
    addIndexes(indexAdder, sourceLib, excludeIndexNames);
    if (indexAdder.size() > 0) {
      indexAdder.doAddIndexes();
    }
  }

  protected static boolean isDetachableLibrary(XhiveLibraryIf library) {
    return (library.getOptions() & XhiveLibraryIf.DETACHABLE_LIBRARY) == XhiveLibraryIf.DETACHABLE_LIBRARY;
  }

  public static String getDefaultSegmentId(String retentionLibPath) {
    return retentionLibPath.replace('/', '-');
  }

  private static String getNewSegmentID(XhiveDatabaseIf database, String segmentId) {
    String result = segmentId;
    int number = 0;
    while (database.hasSegment(result)) {
      number++;
      result = segmentId + "-" + number;
    }
    return result;
  }

  public static XhiveXQueryQueryIf createXQuery(XhiveSessionIf session, String expression) {
    XhiveXQueryCompilerIf compiler = session.getXQueryCompiler();
    OperationsUtil.registerXQueryFunctions(compiler);
    XhivePreparedQueryIf preparedQuery = compiler.prepareQuery(expression);
    XhiveXQueryQueryIf query = preparedQuery.createQuery(session);
    return query;
  }

  private static void registerXQueryFunctions(final XhiveXQueryCompilerIf compiler) {
    compiler.setFunction(DDSXQueryUtils.XQUERY_FUNCTION_GENERATE_URI, 1,
        new DDSURIExtensionFunction(DDS.getApplication()));
    compiler.setFunction(DDSXQueryUtils.XQUERY_FUNCTION_METADATA, 2, new DDSMetadata());
    compiler.setFunction(XQUERY_FUNCTION_ATTACH_LIBRARIES, 2, new AttachLibraries());
    compiler.setFunction(XQUERY_FUNCTION_DETACH_LIBRARIES, 2, new DetachLibraries());
    compiler.setFunction(XQUERY_FUNCTION_READ_FROM_CENTERA, 2, new ReadFromCentera());
    compiler.setFunction(XQUERY_FUNCTION_WRITE_TO_CENTERA, 1, new WriteToCentera());
    compiler.setFunction(XQUERY_FUNCTION_CREATE_DETACHABLE_LIBRARY, 4,
        new CreateDetachableLibrary());
  }

  private OperationsUtil() {
    // Utility class
  }
}
