import services.UserInteractionService;

public class ChatBotApp {
    public static void main(String[] args) {

        UserInteractionService uiService = new UserInteractionService();
        uiService.start();
    }
}
