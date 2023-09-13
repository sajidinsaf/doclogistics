package com.attuned.documentdelivery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class PdfTextExtractor {

    public Map<String, Object> processRecord(InputStream input) {

        Map<String, Object> map = new HashMap<String, Object>();
        try {

                try {

                    ContentHandler handler = new BodyContentHandler();
                    Metadata metadata = new Metadata();
                    Parser parser = new AutoDetectParser();
                    // Parser parser = new PDFParser();
                    ParseContext parseContext = new ParseContext();
                    parser.parse(input, handler, metadata, parseContext);
             

                    map.put("text", handler.toString());//.replaceAll("\n|\r|\t", " "));
                    map.put("title", metadata.get(TikaCoreProperties.TITLE));
                    map.put("pageCount", metadata.get("xmpTPg:NPages"));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return map;
    }
    
    public HttpEntity getWebEntity(String url) throws ClientProtocolException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return entity;
    }

    public static void main(String args[]) throws Exception {

        InputStream input = new FileInputStream(args[0]);
        
        PdfTextExtractor webPagePdfExtractor = new PdfTextExtractor();
        Map<String, Object> extractedMap = webPagePdfExtractor.processRecord(input);
        System.out.println(extractedMap.get("text"));
        System.out.println(extractedMap.get("title"));
        System.out.println(extractedMap.get("pageCount"));
    }

}
