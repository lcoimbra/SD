package action;

import beans.*;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;

/**
 * Created by kifel on 08/12/2016.
 */
public class DetailAuctionAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String Id;
    private AuctionBean ab = new AuctionBean();


    //http://stackoverflow.com/questions/2960265/implement-ebay-finding-feedback-api
    //http://developer.ebay.com/Devzone/finding/HowTo/GettingStarted_JS_NV_JSON/GettingStarted_JS_NV_JSON.html
    public final static String EBAY_APP_ID = " _____ ";
    public final static String EBAY_FINDING_SERVICE_URI = "https://svcs.ebay.com/services/search/FindingService/v1?"
            + "SECURITY-APPNAME={applicationId}" +
            "&OPERATION-NAME={operation}" +
            "&SERVICE-VERSION={version}" +
            "&RESPONSE-DATA-FORMAT=XML" +
            "&REST-PAYLOAD" +
            "&keywords={keywords}" +
            "&paginationInput.entriesPerPage=1" +
            "&GLOBAL-ID={globalId}" +
            "&siteid=186" +
            "&itemFilter.value=FixedPrice" +
            "&sortOrder=PricePlusShippingLowest";
    public static final String SERVICE_VERSION = "1.0.0";	//versao da API
    public static final String OPERATION_NAME = "findItemsByKeywords"; //nome da operacao que vamos executar
    public static final String GLOBAL_ID = "EBAY-ES";	//vamos a loja do EBAY em Espanha

    @Override
    public String execute() {
        if(Id!=null && Id!="" && isInteger(Id)){
            this.ab.setId(this.Id);
            if(this.session.containsKey("authenticationBean")) {
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                this.ab.setRmi(authb.getRmi());

            }else{
                return "erro1"; //caso não exista authenticationBean quer dizer que não esta logado;
            }
            String res = this.ab.detailAuction();
            if(res.contains("ok: true")){
                session.put("auction",ab);

                /*vamos preparar as coisas para ir ao EBAY*/
                try {
                    //criar o URL
                    URL url = new URL(createAddress(this.ab.getTitle().replaceAll(" ", ",")));//temos de por aqui o nome do produto do leilao a procurar no ebay
                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        // HTTP Verb
                        connection.setRequestMethod("GET");
                        // Get requests data from the server.

                        // We are interested in the output
                        connection.setDoOutput(true);

                        // If there is a 3xx error, we want to know.
                        connection.setInstanceFollowRedirects(false);

                        // The Accept header defines what kind of formats we are interested in.
                        connection.setRequestProperty("Accept", "application/xml");
                        // You should play with "*/*", "application/xml" and "application/json"
                        // JSON might need a third party library to parse the response.

                        // User Agent is the name of your application.
                        connection.setRequestProperty("User-agent", "sd2016testAPP");
                        // Some of the most common are Mozilla, Internet Explorer and GoogleBot.


                        System.out.println("Connection code: " + connection.getResponseCode());
                        // If we get a Redirect or an Error (3xx, 4xx and 5xx)
                        if (connection.getResponseCode() >= 300) {
                            // We want more information about what went wrong.
                            debug(connection);
                        }

                        String line;
                        long totalBytes = 0;

                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                            totalBytes += line.getBytes("UTF-8").length ;
                            //System.out.println("Total bytes read ::  " + totalBytes);
                        }

                        String response = builder.toString();
                        XPath xpath = XPathFactory.newInstance().newXPath();
                        InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
                        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder2 = domFactory.newDocumentBuilder();


                        Document doc = builder2.parse(is);
                        XPathExpression ackExpression = xpath.compile("//findItemsByKeywordsResponse/ack");
                        XPathExpression itemExpression = xpath.compile("//findItemsByKeywordsResponse/searchResult/item");

                        String ackToken = (String) ackExpression.evaluate(doc, XPathConstants.STRING);
                        int i = 0;
                        if (ackToken.equals("Success")) {
                            NodeList nodes = (NodeList) itemExpression.evaluate(doc, XPathConstants.NODESET);
                            System.out.println("len " + nodes.getLength());
                            for (i = 0; i < nodes.getLength(); i++) {

                                Node node = nodes.item(i);
                                //String title = (String) xpath.evaluate("title", node, XPathConstants.STRING);
                                //String itemUrl = (String) xpath.evaluate("viewItemURL", node, XPathConstants.STRING);
                                String currentPrice = (String) xpath.evaluate("sellingStatus/currentPrice", node, XPathConstants.STRING);

                                this.session.put("eBayPrice", currentPrice);
                                System.out.println("entrei: " + currentPrice);
                            }
                        }
                        if (i == 0) {
                            System.out.println("sdasd");
                            if (this.session.containsKey("eBayPrice")) {
                                this.session.remove("eBayPrice");
                            }
                        }


                        is.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                return SUCCESS;
            }
        }
        return ERROR;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private String createAddress(String tag) {

        //substitute token
        String address = DetailAuctionAction.EBAY_FINDING_SERVICE_URI;
        address = address.replace("{version}", DetailAuctionAction.SERVICE_VERSION);
        address = address.replace("{operation}", DetailAuctionAction.OPERATION_NAME);
        address = address.replace("{globalId}", DetailAuctionAction.GLOBAL_ID);
        address = address.replace("{applicationId}", DetailAuctionAction.EBAY_APP_ID);
        address = address.replace("{keywords}", tag);
        System.out.println("URL CRIADO: "+address);
        return address;

    }

    private void debug(HttpURLConnection connection) throws IOException {
        // This function is used to debug the resulting code from HTTP connections.

        // Response code such as 404 or 500 will give you an idea of what is wrong.
        System.out.println("Response Code:" + connection.getResponseCode());

        // The HTTP headers returned from the server
        System.out.println("_____ HEADERS _____");
        for ( String header : connection.getHeaderFields().keySet()) {
            System.out.println(header + ": " + connection.getHeaderField(header));
        }

        // If there is an error, the response body is available through the method
        // getErrorStream, instead of regular getInputStream.
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            builder.append(inputLine);
        in.close();
        System.out.println("Body: " + builder);
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

}
