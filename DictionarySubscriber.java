import com.thomsonreuters.ema.access.*;
import com.thomsonreuters.ema.rdm.DataDictionary;
import com.thomsonreuters.ema.rdm.DictionaryUtility;
import com.thomsonreuters.ema.rdm.DictionaryCallbackClient;

public class DictionarySubscriber {

    public static void main(String[] args) {
        try {
            // Create an EMA configuration
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig();

            // Set the connection details to the ADS server
            config.host("adsServerHostname")
                  .port("adsServerPort")
                  .username("yourUsername")
                  .password("yourPassword")
                  .tcpNodelay(true);

            // Create an OmmConsumer
            OmmConsumer consumer = EmaFactory.createOmmConsumer(config);

            // Specify the dictionary name you want to download
            String dictionaryName = "RWFFld"; // Replace with the desired dictionary name

            // Create a DictionaryRequest for downloading the dictionary
            ReqMsg dictionaryRequest = EmaFactory.createReqMsg();
            dictionaryRequest.domainType(EmaRdm.MMT_DICTIONARY);
            dictionaryRequest.filter(EmaRdm.DICTIONARY_NORMAL);
            dictionaryRequest.name(dictionaryName);

            // Register a DictionaryCallbackClient to handle the dictionary response
            DictionaryCallbackClient dictionaryCallbackClient = new DictionaryCallbackClient();
            consumer.registerClient(dictionaryRequest, dictionaryCallbackClient);

            // Create a MarketPrice request for real-time data
            ReqMsg marketPriceRequest = EmaFactory.createReqMsg();
            marketPriceRequest.domainType(EmaRdm.MMT_MARKET_PRICE);
            marketPriceRequest.serviceName("DIRECT_FEED"); // Replace with the appropriate service name
            marketPriceRequest.name("AAPL.O"); // Replace with the desired instrument name

            // Register a MarketPriceCallbackClient to handle real-time data
            MarketPriceCallbackClient marketPriceCallbackClient = new MarketPriceCallbackClient();
            consumer.registerClient(marketPriceRequest, marketPriceCallbackClient);

            // Main event loop to process messages
            while (true) {
                consumer.dispatch(1000); // Dispatch messages for processing
            }
        } catch (OmmException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Define a callback client to handle the dictionary response
    static class DictionaryCallbackClient implements OmmConsumerClient {

        @Override
        public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
            if (refreshMsg.payload().dataType() == DataTypes.SERIES) {
                DataDictionary dataDictionary = EmaFactory.createDataDictionary();
                DictionaryUtility.decodeFieldDictionary(refreshMsg.payload().series(), dataDictionary);
                System.out.println("Dictionary download completed successfully.");
            }
        }

        @Override
        public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {
            // Handle dictionary updates if needed
        }

        @Override
        public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent event) {
            if (statusMsg.hasState() && statusMsg.state().streamState() == OmmState.StreamState.CLOSED_RECOVER) {
                System.out.println("Dictionary stream closed. Exiting.");
                System.exit(0);
            }
        }
    }

    // Define a callback client to handle real-time market data
    static class MarketPriceCallbackClient implements OmmConsumerClient {

        @Override
        public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent event) {
            // Handle real-time market data
            System.out.println("Received MarketPrice update: " + refreshMsg.toString());
        }

        @Override
        public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent event) {
            // Handle real-time market data updates if needed
        }

        @Override
        public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent event) {
            if (statusMsg.hasState() && statusMsg.state().streamState() == OmmState.StreamState.CLOSED_RECOVER) {
                System.out.println("MarketPrice stream closed. Exiting.");
                System.exit(0);
            }
        }
    }
}
