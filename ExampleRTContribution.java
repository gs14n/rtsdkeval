import com.thomsonreuters.ema.access.*;
import com.thomsonreuters.ema.rdm.DataDictionary;
import com.thomsonreuters.ema.rdm.EmaRdm;

public class ContributorExample {
    public static void main(String[] args) {
        OmmConsumer consumer = EmaFactory.createOmmConsumer(EmaFactory.createOmmConsumerConfig());
        
        ReqMsg reqMsg = EmaFactory.createReqMsg();
        reqMsg.domainType(EmaRdm.MMT_MARKET_PRICE);
        reqMsg.serviceName("ELEKTRON_EDGE");  // Replace with the appropriate service name
        reqMsg.name("AAPL.O");  // Replace with the desired instrument name
        reqMsg.contribute("CONTRIBUTOR_NAME");  // Replace with the desired contributor name
        
        // Register a callback to handle the received data
        consumer.registerClient(reqMsg, new MyClient());

        while (true) {
            consumer.dispatch(1000); // Dispatch received messages for processing
        }
    }
}

class MyClient implements OmmConsumerClient {
    @Override
    public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent consumerEvent) {
        // Handle refresh messages from the contributor
    }

    @Override
    public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent consumerEvent) {
        // Handle update messages from the contributor
    }

    @Override
    public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent consumerEvent) {
        // Handle status messages (e.g., connection status) from the contributor
    }

    @Override
    public void onGenericMsg(GenericMsg genericMsg, OmmConsumerEvent consumerEvent) {
        // Handle generic messages from the contributor
    }
}
