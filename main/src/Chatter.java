import com.rabbitmq.client.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Chatter {

    private static final String EXCHANGE_NAME = "general";
    private static String nickname;
    private static ArrayList<String> joinMsgs;

    public static void main(String[] argv) throws Exception {
        addJoinMessages();
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

        String joinMsg = String.format(generateRandomJoinMessage(), nickname);
        channel.basicPublish(EXCHANGE_NAME, "", null, joinMsg.getBytes("UTF-8"));

        while(true){
            String msg = s.nextLine();

            if(msg.startsWith("/")){ //Command
                int finish;
                if(msg.indexOf(' ') != -1){
                    finish = msg.indexOf(' ');
                } else {
                    finish = msg.length();
                }
                switch(msg.substring(1, finish)){
                    case "w":
                        String[] params = msg.split("\\s+");
                        if(params.length < 3){
                            System.out.println("USO: /w Nome Mensagem");
                            System.out.println("Exemplo: /w Victor Ola, esta e uma mensagem privada!");
                        }
                        break;
                    default:
                        System.out.println("Este comando (/"+ msg.substring(1, finish) +") nao existe!");
                        break;
                }
            } else {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String sDate = sdf.format(date);

                String finalMsg = "[" + sDate + "] " + nickname + ": " + msg;

                channel.basicPublish(EXCHANGE_NAME, "", null, finalMsg.getBytes("UTF-8"));
            }
        }

        //channel.close();
        //conn.close();
    }

    private static String generateRandomJoinMessage() {
        return joinMsgs.get((new Random()).nextInt(joinMsgs.size()));
    }

    private static void addJoinMessages() {
        joinMsgs = new ArrayList<String>();

        joinMsgs.add("Saiam daqui! %s acabou de chegar.");
        joinMsgs.add("Pessoal, %s entrou. Finjam que estao ocupados.");
        joinMsgs.add("Eu desisto. Ate o %s agora?");
        joinMsgs.add("%s chegou atrasado, pra variar.");
        joinMsgs.add("Shhhh! %s acabou de entrar.");
    }

}