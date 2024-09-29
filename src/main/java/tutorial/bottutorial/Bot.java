package tutorial.bottutorial;

import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    
    public static Long AdminID = 203465746L;
    private InlineKeyboardMarkup keyboardN1;
    private InlineKeyboardMarkup keyboardN2;
    private InlineKeyboardMarkup keyboardN3;
    public boolean selectedSupport = false;
    
    @Override
    public String getBotUsername() {
        return "testingbotsworkingbot";
    }
    
    @Override
    public String getBotToken() {
        return "7875632797:AAFV-brQ042il71kgWMG-Szx6EkQ8Wa-B2k";
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().equals("/start")) {
            handleIncomingMessage(update);
        } else if (update.hasCallbackQuery()) {
            buttonTap(update);
        } else if (update.getMessage().getText().equals("/start")) {
            handleStart(update);
        }
    }
    
    
    public void handleIncomingMessage(Update update) {
        var chatID = update.getMessage().getChatId();
        
        if (chatID == AdminID.longValue()) {
            var msg = update.getMessage().getText();
            var msgRepliedForwardId = update.getMessage().getReplyToMessage().getForwardFrom().getId();
            sendText(msgRepliedForwardId, "\uD83D\uDC6E Nuovo messaggio da un admin:\n" + msg);
        }
        
        if (chatID != AdminID.longValue()) {
            if (selectedSupport == true) {
                forwardText(AdminID, chatID, update.getMessage().getMessageId());
            } else {
                sendText(chatID, "\u26A0 Attenzione! Per accedere al supporto selezionare l'opzione nel menu!");
            }
        }
    }
    
    public void handleStart(Update update) {
        var chatId = update.getMessage().getChatId();
        
        var messaggioTest = InlineKeyboardButton.builder()
                                 .text("\uD83C\uDF45 Test").callbackData("test")
                                 .build();
        
        var messaggioProva = InlineKeyboardButton.builder()
                             .text("\uD83C\uDF55 Prova").callbackData("prova")
                             .build();
        
        var messaggioSupporto = InlineKeyboardButton.builder()
                                .text("\uD83D\uDD30 Supporto").callbackData("supporto")
                                .build();
        
        var messaggioChiudi = InlineKeyboardButton.builder()
                              .text("\uD83D\uDCF5 Chiudi").callbackData("chiudi")
                              .build();
        
        var messaggioIndietro = InlineKeyboardButton.builder()
                                .text("\u21A9 Indietro").callbackData("indietro")
                                .build();
        
        keyboardN1 = InlineKeyboardMarkup.builder()
                     .keyboardRow(List.of(messaggioTest, messaggioProva))
                     .keyboardRow(List.of(messaggioSupporto))
                     .build();
        
        keyboardN2 = InlineKeyboardMarkup.builder()
                     .keyboardRow(List.of(messaggioTest, messaggioProva))
                     .keyboardRow(List.of(messaggioIndietro))
                     .build();
        
        keyboardN3 = InlineKeyboardMarkup.builder()
                     .keyboardRow(List.of(messaggioChiudi))
                     .build();
        
        sendMenu(chatId, "\u2B50 <b>Benvenuto nel testBot!</b>\n\uD83D\uDCA1 Ecco alcune <i>possibili</i> scelte!", keyboardN1);
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                         .chatId(who.toString())
                         .text(what).build();
        
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void forwardText(Long who, Long chatID, int msgId) {
        ForwardMessage ft = ForwardMessage.builder()
                            .chatId(who.toString())
                            .fromChatId(chatID)
                            .messageId(msgId).build();
        
        try {
            execute(ft);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder()
                         .chatId(who.toString())
                         .parseMode("HTML").text(txt)
                         .replyMarkup(kb).build();
        
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Update update) {
        
        var id = update.getCallbackQuery().getMessage().getChatId();
        var msgId = update.getCallbackQuery().getMessage().getMessageId();
        var data = update.getCallbackQuery().getData();
        var queryId = update.getCallbackQuery().getId();
        
        EditMessageText newTxt = EditMessageText.builder()
                        .chatId(id.toString())
                        .messageId(msgId).text("").build();
        
        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                                       .chatId(id.toString()).messageId(msgId).build();
        
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                                    .callbackQueryId(queryId).build();
        
        if (data.equals("test") || data.equals("prova")) {
            newTxt.setText("\uD83D\uDDFF Ecco la risposta per i pulsanti test e prova \uD83D\uDDFF");
            newKb.setReplyMarkup(keyboardN2);
        } else if (data.equals("chiudi")) {
            newTxt.setText("\u2B50 Benvenuto nel bot!\n\uD83D\uDCA1 Ecco alcune possibili scelte!");
            newKb.setReplyMarkup(keyboardN1);
            selectedSupport = false;
        } else if (data.equals("indietro")) {
            newTxt.setText("\u2B50 Benvenuto nel bot!\n\uD83D\uDCA1 Ecco alcune possibili scelte!");
            newKb.setReplyMarkup(keyboardN1);
        } else if (data.equals("supporto")) {
            newTxt.setText(("\uD83D\uDD38 Sarai ora connesso alla chat con un moderatore! \uD83D\uDD38"));
            newKb.setReplyMarkup(keyboardN3);
            selectedSupport = true;
        }
        
        try {
            execute(close);
            execute(newTxt);
            execute(newKb);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    
}
