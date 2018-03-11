package nl.robenanita.googlemapstest.SimConnection;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.robenanita.googlemapstest.DataType;
import nl.robenanita.googlemapstest.Offset;

/**
 * Created by Rob Verhoef on 12-1-14.
 */
public class FSUIPCConnection {
    public FSUIPCConnection(String IP, int Port, boolean webapi)
    {
        ip = IP;
        port = Port;
        offsets = new ArrayList<Offset>();
        this.webapi = webapi;
    }

    private String TAG = "GooglemapsTest";
    private String ip;
    private int port;
    private boolean webapi;

    private boolean m_opened = false;
    private boolean m_processed = false;
    private boolean m_connected = false;

    private List<Offset> offsets;

    public boolean isConnected()
    {
        if (webapi)
        {
            return (mWebApiClient == null) ? false : mWebApiClient.isConnected();
        }
        else {
            return (mTcpClient == null) ? false : mTcpClient.isConnected();
        }
    }

    private TCPClient mTcpClient = null;
    private WebAPIClient mWebApiClient = null;
    private connectTask conctTask = null;

    private OnFSUIPCAction mFSUIPCConnectedListener = null;
    public void SetFSUIPCConnectedListener(OnFSUIPCAction listener) {mFSUIPCConnectedListener = listener;}
    private OnFSUIPCOpen mFSUIPCOpenedListener = null;
    public void SetFSUIPCOpenListener(OnFSUIPCOpen listener) {mFSUIPCOpenedListener = listener;}
    private OnFSUIPCAction mFSUIPCClosedListener = null;
    public void SetFSUIPCCloseListener(OnFSUIPCAction listener) {mFSUIPCClosedListener = listener;}
    private OnFSUIPCAction mFSUIPCProcessedListener = null;
    public void SetFSUIPCProcessListener(OnFSUIPCAction listener) {mFSUIPCProcessedListener = listener;}
    private OnFSUIPCRead mFSUIPCReadListener = null;
    public void SetFSUIPCReadListener(OnFSUIPCRead listener) {mFSUIPCReadListener = listener;}
    private OnFSUIPCAction mFSUIPCErrorListener = null;
    public void SetFSUIPCErrorListener(OnFSUIPCAction listener) {mFSUIPCErrorListener = listener;}

    private Document newXmlDocument()
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // root elements
        Document doc;
        if (docBuilder == null)
        {
            doc = null;
        }
        else {
            doc = docBuilder.newDocument();
        }
        return doc;
    }

    private String getXMLString(Document doc)
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        String output = "";
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerConfigurationException e) {
            Log.e(TAG, e.getMessage());
        } catch (TransformerException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    public boolean Connect()
    {
        mTcpClient = null;
        mWebApiClient = null;
        // connect to the server
        if (webapi)
        {
            if (mFSUIPCConnectedListener != null) mFSUIPCConnectedListener.FSUIPCAction("Connected", true);
        }
        else {
            conctTask = new connectTask();
            conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return true;
    }

    public boolean Open()
    {
        if (webapi)
        {
            mWebApiClient = new WebAPIClient(ip, port);
            mWebApiClient.SetFSUIPCOpenListener(new OnFSUIPCOpen() {
                @Override
                public void FSUIPCOpen(String message, boolean success, String version) {
                    if (success) {
                        if (mFSUIPCOpenedListener != null)
                            mFSUIPCOpenedListener.FSUIPCOpen(message, success, version);
                    }
                    else {
                        if (mFSUIPCErrorListener != null) {
                            mFSUIPCErrorListener.FSUIPCAction(message, false);
                        }
                    }
                }
            });
//            mWebApiClient.SetFSUIPCOpenListener(new OnFSUIPCAction() {
//                @Override
//                public void FSUIPCAction(String message, boolean success) {
//                    if (success) {
//                        if (mFSUIPCOpenedListener != null)
//                            mFSUIPCOpenedListener.FSUIPCAction(message, success);
//                    }
//                    else {
//                        if (mFSUIPCErrorListener != null) {
//                            mFSUIPCErrorListener.FSUIPCAction(message, false);
//                        }
//                    }
//
//                }
//            });
            return mWebApiClient.OpenFSUIPC();
        }
        else {
            // <root>
            //  <FSUIPC Command="Open" />
            // </root>

            Document doc = newXmlDocument();
            if (doc != null) {
                Element rootElement = doc.createElement("root");
                doc.appendChild(rootElement);

                Element fsuipc = doc.createElement("FSUIPC");
                rootElement.appendChild(fsuipc);

                fsuipc.setAttribute("Command", "Open");

                String cmd = getXMLString(doc);
                Log.i(TAG, "Open XML string: " + cmd);

                mTcpClient.sendMessage(cmd);
                //mFSUIPCOpenedListener.FSUIPCAction("FSUIPC Connection Opened", true);

                return true;
            } else
                return false;
        }
    }

    public boolean Close()
    {
        if (webapi)
        {
            mWebApiClient = new WebAPIClient(ip, port);
            mWebApiClient.SetFSUIPCCloseListener(new OnFSUIPCAction() {
                @Override
                public void FSUIPCAction(String message, boolean success) {
                    if (success)
                        if (mFSUIPCClosedListener != null)  mFSUIPCClosedListener.FSUIPCAction("FSUIPC Connection Closed", true);
                    else
                        if (mFSUIPCErrorListener != null) {
                            if (mFSUIPCClosedListener != null)  mFSUIPCClosedListener.FSUIPCAction("FSUIPC Connection Closed", true);
                            mFSUIPCErrorListener.FSUIPCAction(message, false);
                        }
                }
            });
            return mWebApiClient.CloseFSUIPC();
        }
        else {
            // <root>
            //  <FSUIPC Command="Close" />
            // </root>

            Document doc = newXmlDocument();
            if (doc != null) {
                Element rootElement = doc.createElement("root");
                doc.appendChild(rootElement);

                Element fsuipc = doc.createElement("FSUIPC");
                rootElement.appendChild(fsuipc);

                fsuipc.setAttribute("Command", "Close");

                String cmd = getXMLString(doc);
                //Log.i(TAG, "Close XML string: " + cmd);

                if (mTcpClient != null) {
                    mTcpClient.sendMessage(cmd);
                    mTcpClient.stopClient();
                    mFSUIPCClosedListener.FSUIPCAction("FSUIPC Connection Closed", true);
                }

                return true;
            } else
                return false;
        }
    }

    public boolean Process(String DatagroupName)
    {
        if (webapi)
        {
            mWebApiClient = new WebAPIClient(ip, port);
            mWebApiClient.SetFSUIPCProcessListener(new OnFSUIPCAction() {
                @Override
                public void FSUIPCAction(String message, boolean success) {
                    if (success)
                    {
                        if (success) {
                            ReadOffsetsFromJson(message);
                            if (mFSUIPCProcessedListener != null)
                                mFSUIPCProcessedListener.FSUIPCAction("Processed", true);
                        }
                        else
                            if (mFSUIPCErrorListener != null) {
                                mFSUIPCErrorListener.FSUIPCAction(message, false);
                            }
                    }
                }
            });
            mWebApiClient.ProcessFSUIPCOffsets(offsets);
            return false;
        }
        else {
            // <root>
            //      <FSUIPC Command="ReadOffset" Offset="" Datagroup="" />
            //      <FSUIPC Command="ReadOffset" Offset="" Datagroup="" />
            // </root>

            Document doc = newXmlDocument();
            if (doc != null) {
                Element rootElement = doc.createElement("root");
                doc.appendChild(rootElement);

                Element fsuipc = doc.createElement("FSUIPC");
                rootElement.appendChild(fsuipc);
                fsuipc.setAttribute("Command", "ReadOffset");
                Element offs = doc.createElement("Offsets");
                fsuipc.appendChild(offs);

                for (Offset o : offsets) {
                    Element off = doc.createElement("Offset");
                    offs.appendChild(off);

                    off.setAttribute("Address", Integer.toString(o.Address));
                    off.setAttribute("Datagroup", o.DatagroupName);
                    off.setAttribute("Datatype", o.Datatype.toString());
                }

                String cmd = getXMLString(doc);
                //Log.i(TAG, "ReadOffsets XML string: " + cmd);

                mTcpClient.sendMessage(cmd);

                return true;

            } else
                return false;
        }
    }

    public Object ReadOffset(int Address)
    {
        Offset off = findOffsetByAddress(Address);

        return off.Value;
    }

    public void AddOffset(int Address, String DatagroupName, DataType Datatype)
    {
        Offset o = new Offset();
        o.Address = Address;
        o.Datatype = Datatype;
        o.DatagroupName = DatagroupName;
        offsets.add(o);
    }

    public boolean WriteOffset(int Address, DataType Type, Object Value)
    {
        String objectType = Object.class.getSimpleName();
        // <root>
        //  <FSUIPC Command="WriteOffset" DataType"" Offset="" >
        //      Value
        //  </FSUIPC >
        // </root>

        return false;
    }

    private Offset findOffsetByAddress(int Address)
    {
        for (Offset o : offsets)
        {
            if (o.Address == Address)
                return  o;
        }
        return null;
    }

    private void ReadOffsetsFromJson(String json)
    {
        try {
            JSONArray resp_offsets = new JSONArray(json);

            resp_offsets.length();

            for (int i=0; i< resp_offsets.length(); i++)
            {
                JSONObject _offset = resp_offsets.getJSONObject(i);
                Integer address = _offset.getInt("Address");
                Offset o = findOffsetByAddress(address);
//                if (_offset.getString("DataType").equals("Double"))
//                    o.Value = _offset.getDouble("Value");
//                else
                    o.Value = _offset.get("Value").toString().replace(',', '.');
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readOffsetsFromXML(Node offsets)
    {
        NodeList nodes = offsets.getChildNodes();
        int count = nodes.getLength();
        for (int i=0; i<count; i++)
        {
            Node n = nodes.item(i);

            //if(nodes.item(i).getNodeName()=="Offset")
            //{
            NamedNodeMap arts = nodes.item(i).getAttributes();
            String v = arts.getNamedItem("Address").getNodeValue();
            int addr = Integer.parseInt(v);
            Offset o = findOffsetByAddress(addr);
            if (o != null)
            {
                String nn = n.getTextContent();
                o.Value = nn;
                //Log.i(TAG, "Found value: " + o.Value.toString() + " for offset address: " + Integer.toString(addr));
            }
            //}
        }
    }

    public interface OnFSUIPCAction {
        public void FSUIPCAction(String message, boolean success);
    }

    public interface OnFSUIPCRead {
        public void FSUIPCRead(String message, int offset, Object value, boolean success);
    }

    public interface OnFSUIPCOpen {
        public void FSUIPCOpen(String message, boolean success, String version);
    }

    public Document loadXMLFromString(String xml)
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            try {
                doc = builder.parse(is);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private class connectTask extends AsyncTask<String,String,TCPClient> {
        public String ConnectMessage;
        @Override
        protected TCPClient doInBackground(String... message)
        {
            //we create a TCPClient object and

            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    try
                    {
                        //this method calls the onProgressUpdate
                        if(message!=null)
                        {
                            //Log.i(TAG, "Return Message from Socket::::: >>>>> "+message );
                            publishProgress(message);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            });

            mTcpClient.SERVERIP = ip;
            mTcpClient.SERVERPORT = port;
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onPostExecute(TCPClient tcpClient) {
            super.onPostExecute(tcpClient);
            Log.i(TAG, "onPostExecute connectTask");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String retMessage = values[0].toString();
            //Log.i(TAG, "OnProgressUpdate: " + retMessage);

            // Here we have to read the return message from the server and update the offsets list.
            Document retDoc = loadXMLFromString(retMessage);

            NodeList nodes = retDoc.getElementsByTagName("ERROR");
            if (nodes.getLength() > 0) {
                //Log.i(TAG, "Fire FSUIPCErrorListener");
                if (mFSUIPCErrorListener != null) {
                    String m = nodes.item(0).getTextContent();
                    mFSUIPCErrorListener.FSUIPCAction(m, false);
                }
            }

            nodes = retDoc.getElementsByTagName("Connected");
            if (nodes.getLength() > 0) {
                //Log.i(TAG, "Fire FSUIPCConnectedListener");
                if (mFSUIPCConnectedListener != null)
                    mFSUIPCConnectedListener.FSUIPCAction("Connected", true);
            }

            nodes = retDoc.getElementsByTagName("FSUIPC_Opened");
            if (nodes.getLength() > 0) {
                //Log.i(TAG, "Fire FSUIPCOpenedListener");
                if (mFSUIPCOpenedListener != null)
                    mFSUIPCOpenedListener.FSUIPCOpen("Opened", true, "");
            }

            nodes = retDoc.getElementsByTagName("FSUIPC_Closed");
            if (nodes.getLength() > 0) {
                //Log.i(TAG, "Fire FSUIPCClosedListener");
                if (mFSUIPCClosedListener != null)
                    mFSUIPCClosedListener.FSUIPCAction("Closed", true);
            }

            nodes = retDoc.getElementsByTagName("OffsetsRead");
            if (nodes.getLength() > 0) {
                //Log.i(TAG, "OffsetsRead received so read the offsets values");
                readOffsetsFromXML(nodes.item(0));
                if (mFSUIPCProcessedListener != null)
                    mFSUIPCProcessedListener.FSUIPCAction("Processed", true);
            }

            nodes = retDoc.getElementsByTagName("ConnectError");
            if (nodes.getLength() > 0)
            {
                if (mFSUIPCErrorListener != null) {
                    String m = nodes.item(0).getTextContent();
                    mFSUIPCErrorListener.FSUIPCAction(m, false);
                }
            }


        }


    }

}
