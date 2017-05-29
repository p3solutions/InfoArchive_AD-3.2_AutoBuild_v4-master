/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.query;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import com.xhive.XhiveDriverFactory;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.XhiveFtsTextHandlerIf;
import com.xhive.util.interfaces.XhiveFtsUtilIf;

public class FtsExcerptExtensionFunction implements XhiveXQueryExtensionFunctionIf {

  public static final String SEPARATOR = " ";

  private static final int MAX_EXCERPT_SIZE = 50;
  private static final int MAX_EXCERPTS = 3;

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    String query = args[0].next().asString();
    Node node = args[1].next().asNode();
    String nodeVal = getSeparatedString(node);
    StringBuffer sb = new StringBuffer();
    if (nodeVal != null) {
      if (query.equals("*")) {
        sb.append(nodeVal);
      } else {
        StreamHighLighter highlighter = new StreamHighLighter(sb);
        getExcerpt(new ExcerptFilter(highlighter, MAX_EXCERPT_SIZE, MAX_EXCERPTS, false), query,
            nodeVal);
      }
    }
    return new String[] { sb.toString() };
  }

  private String getSeparatedString(Node node) {
    XhiveDocumentIf ownerDoc =
        node instanceof XhiveDocumentIf ? (XhiveDocumentIf)node : (XhiveDocumentIf)node
            .getOwnerDocument();
    NodeIterator nodeIterator = ownerDoc.createNodeIterator(node, NodeFilter.SHOW_TEXT, null, true);
    Node textNode = null;
    StringBuffer sb = new StringBuffer();
    while ((textNode = nodeIterator.nextNode()) != null) {
      String value = textNode.getNodeValue();
      if (sb.length() > 0) {
        sb.append(SEPARATOR);
      }
      sb.append(value);
    }
    return sb.toString();
  }

  private class StreamHighLighter implements XhiveFtsTextHandlerIf {

    StringBuffer sb;

    public StreamHighLighter(StringBuffer sb) {
      this.sb = sb;
    }

    private void write(boolean escape, String s) {
      sb.append(s);
    }

    @Override
    public void start() {
    }

    @Override
    public void term(String s) {
      write(false, "<b>");
      write(true, s);
      write(false, "</b>");
    }

    @Override
    public void text(String s) {
      write(true, s);
    }

    @Override
    public void finish() {
      //
    }

  }

  public static XhiveFtsUtilIf getFtsUtil() {
    return XhiveDriverFactory.getDriver().getFtsUtil();
  }

  public static void getExcerpt(XhiveFtsTextHandlerIf handler, String query, String value) {
    getFtsUtil().getTextAndTerms(value, FtsQueryStringEncoder.encode(query), null, handler, true);
  }

  private static final class FtsQueryStringEncoder {

    private FtsQueryStringEncoder() {
      //
    }

    /**
     * Encode a full text search (FTS) query string (see #1684).
     * @param queryString The FTS query string to encode
     * @return The encoded FTS query string, with special characters escaped with a backslash
     */
    public static String encode(String queryString) {
      StringBuffer buffer = new StringBuffer(queryString);
      boolean inLiteral = false;
      int i = 0;
      while (i < buffer.length()) {
        if (buffer.charAt(i) == '"' && !inLiteral && isLiteralBegin(buffer.toString().substring(i))) {
          inLiteral = true;
        } else if (buffer.charAt(i) == '"' && inLiteral && isLiteralEnd(buffer.toString(), i)) {
          inLiteral = false;
        } else if (buffer.charAt(i) == '"' || !inLiteral && isSpecialChar(buffer.charAt(i))) {
          buffer.insert(i++, '\\');
        }
        ++i;
      }
      return buffer.toString();
    }

    private static boolean isLiteralBegin(String text) {
      if (text.charAt(0) != '"') {
        return false;
      }
      int index = text.indexOf('"', 1);
      while (index >= 0) {
        if (isLiteralEnd(text, index)) {
          return true;
        }
        index = text.indexOf('"', index + 1);
      }
      return false;
    }

    private static boolean isLiteralEnd(String text, int index) {
      return text.charAt(index - 1) != '\\'
          && (index == text.length() - 1 || text.charAt(index + 1) == ' ');
    }

    private static boolean isSpecialChar(char c) {
      switch (c) {
        case '(':
        case ')':
        case ':':
        case '^':
        case '[':
        case ']':
        case '"':
        case '{':
        case '}':
          /*
           * The following are indeed special characters, but we actually want to use their special
           * meaning, see #1684 case '~': case '+': case '-': case '!': case '*': case '?':
           */
          return true;
        default:
          return false;
      }
    }
  }

  // ADQ: ...You're not the only one that get's a headache from this class....
  private static final class ExcerptFilter implements XhiveFtsTextHandlerIf {

    private static final String ELLIPSIS_STR = "...";
    // -1 = no term found
    private int lastTermOffset = -1;
    private int excerptSize;
    private int maxExcerptSize;
    private int maxExcerpts;
    private int excerpts;
    private StringBuffer buffer = new StringBuffer();
    private XhiveFtsTextHandlerIf wrapped;
    private boolean forceEllipsis;
    private boolean ellipsis;
    private boolean inhibitNextEllipsis; // NOPMD

    private ExcerptFilter(XhiveFtsTextHandlerIf wrapped, int maxExcerptSize, int maxExcerpts,
        boolean forceEllipsis) {
      this.wrapped = wrapped;
      this.maxExcerptSize = maxExcerptSize;
      this.maxExcerpts = maxExcerpts;
      this.forceEllipsis = forceEllipsis;
    }

    private boolean acceptMoreExcerpts() {
      return maxExcerpts == -1 || excerpts + 1 <= maxExcerpts;
    }

    @Override
    public void start() {
      if (forceEllipsis) {
        writeEllipsis(true);
      }
    }

    @Override
    public void term(String term) {
      if (acceptMoreExcerpts()) {
        int startOffset = buffer.length();
        if (excerptSize == 0) {
          int extraChars =
              maxExcerptSize > term.length() ? (maxExcerptSize - term.length()) / 2 : 0;
          boolean textBefore = startOffset > extraChars;
          if (textBefore) {
            // First term in excerpt
            writeEllipsis(false);
          }
          int startIndex = textBefore ? startOffset - extraChars : 0;
          write(buffer.substring(startIndex, startOffset));
          excerptSize = startOffset - startIndex;
        } else {
          // Put text between terms
          if (startOffset > lastTermOffset) {
            String between = buffer.substring(startOffset);
            write(buffer.substring(lastTermOffset));
            startOffset += between.length();
            excerptSize += between.length();
          }
        }
        writeTerm(term);
        excerptSize += term.length();
        lastTermOffset = startOffset;
      }
    }

    @Override
    public void text(String text) {
      if (acceptMoreExcerpts()) {
        buffer.append(text);
        putTextAfterTerm(maxExcerptSize);
      }
    }

    private void putTextAfterTerm(int minSize) {
      int textLengthAfterTerm = buffer.length() - lastTermOffset;
      if (textLengthAfterTerm > minSize && lastTermOffset > -1) {
        int remainingExcerptSize = Math.max(0, maxExcerptSize - excerptSize);
        boolean textAfter = textLengthAfterTerm > remainingExcerptSize;
        int endIndex = lastTermOffset + (textAfter ? remainingExcerptSize : textLengthAfterTerm);
        write(buffer.substring(lastTermOffset, endIndex));
        if (textAfter) {
          writeEllipsis(false);
        }
        // Start new excerpt
        excerpts++;
        excerptSize = 0;
        lastTermOffset = -1;
      }
    }

    @Override
    public void finish() {
      putTextAfterTerm(0);
      if (forceEllipsis && !ellipsis) {
        writeEllipsis(false);
      }
    }

    private void write(String str) {
      ellipsis = false;
      wrapped.text(str);
    }

    private void writeTerm(String term) {
      ellipsis = false;
      wrapped.term(term);
    }

    private void writeEllipsis(boolean inhibitNext) {
      if (ellipsis && inhibitNextEllipsis) {
        inhibitNextEllipsis = inhibitNext;
        return;
      }
      ellipsis = true;
      inhibitNextEllipsis = inhibitNext;
      wrapped.text(ELLIPSIS_STR);
    }

  }

}
