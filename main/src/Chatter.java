import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Chatter {

    private static final String EXCHANGE_NAME = "general";
    private static String nickname; 

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String queueName = channel.queueDeclare().getQueue(); //Random Queue Name
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");

                System.out.println(msg);
            }
        };
        channel.basicConsume(queueName, true, consumer);

        Scanner s = new Scanner(System.in);
        System.out.println("Digite o seu nome:");
        nickname = s.nextLine();

        System.out.println("Seja bem vindo a sala de chat principal, " + nickname + "!");
        while(true){
            String msg = s.nextLine();

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String sDate = sdf.format(date);

            String finalMsg = "[" + sDate + "] " + nickname + ": " + msg;

            channel.basicPublish(EXCHANGE_NAME, "", null, finalMsg.getBytes("UTF-8"));
            //System.out.println(finalMsg);
        }

        //channel.close();
        //conn.close();
    }

}