package com.nothing.onsite.productmanagementzk.viewmodel;

import com.nothing.onsite.productmanagementzk.dao.MessageDao;
import com.nothing.onsite.productmanagementzk.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import java.util.Date;
import java.util.UUID;

@Component
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ChatViewModel {

    @WireVariable
    private MessageDao messageDao;

    private String username;
    private String message;
    private ListModelList<Message> messages = new ListModelList<>();
    private EventQueue<Event> eventQueue;
    private Desktop desktop;
    private final String CHAT_QUEUE_NAME = "chatQueue";
    private Listbox messageListbox;

    @Init
    public void init() {
        try {
            // Tạo tên người dùng ngẫu nhiên
            username = "User_" + UUID.randomUUID().toString().substring(0, 5);

            // Lưu desktop hiện tại
            desktop = Executions.getCurrent().getDesktop();

            // Đăng ký với event queue
            eventQueue = EventQueues.lookup(CHAT_QUEUE_NAME, EventQueues.APPLICATION, true);

            // Thêm listener để nhận tin nhắn
            eventQueue.subscribe(new EventListener<Event>() {
                @Override
                public void onEvent(Event event) {
                    if (event.getData() instanceof Message) {
                        Message chatMessage = (Message) event.getData();

                        // Lưu tin nhắn vào cơ sở dữ liệu
                        try {
                            messageDao.save(chatMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Cập nhật UI từ thread khác
                        if (desktop.isAlive()) {
                            Executions.schedule(desktop, (exec) -> {
                                messages.add(chatMessage);
                                // Cuộn xuống dưới cùng sau khi thêm tin nhắn
                                if (messageListbox != null) {
                                    messageListbox.setSelectedIndex(messages.size() - 1);
                                }
                            }, null);
                        }
                    }
                }
            });

            // Tải tin nhắn cũ từ cơ sở dữ liệu
            try {
                messages.addAll(messageDao.findAll());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Thông báo người dùng mới tham gia
            Message joinMessage = new Message(null, "System", username + " đã tham gia cuộc trò chuyện", new Date());
            eventQueue.publish(new Event("onChat", null, joinMessage));

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi khởi tạo chat: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) org.zkoss.zk.ui.Component view) {
        messageListbox = (Listbox) view.getFellow("messageListbox");
    }

    @Command
    @NotifyChange("message")
    public void sendMessage() {
        try {
            if (message != null && !message.trim().isEmpty()) {
                // Tạo tin nhắn mới
                Message chatMessage = new Message(null, username, message, new Date());

                // Gửi tin nhắn đến tất cả người dùng
                eventQueue.publish(new Event("onChat", null, chatMessage));

                // Xóa tin nhắn sau khi gửi
                message = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi gửi tin nhắn: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Command
    public void changeUsername(@BindingParam("newUsername") String newUsername) {
        try {
            if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(username)) {
                String oldUsername = username;
                
                // Thông báo thay đổi tên người dùng
                Message systemMessage = new Message(null, "System", 
                        oldUsername + " đã đổi tên thành " + newUsername, new Date());
                eventQueue.publish(new Event("onChat", null, systemMessage));
                
                // Cập nhật tên người dùng sau khi gửi thông báo
                username = newUsername;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi đổi tên: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Command
    public void clearChat() {
        try {
            messages.clear();

            // Thông báo xóa chat
            Message systemMessage = new Message(null, "System",
                    username + " đã xóa lịch sử chat", new Date());
            eventQueue.publish(new Event("onChat", null, systemMessage));
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Lỗi khi xóa chat: " + e.getMessage(), "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Command
    public void onClose() {
        try {
            // Thông báo người dùng rời đi
            Message leaveMessage = new Message(null, "System",
                    username + " đã rời khỏi cuộc trò chuyện", new Date());
            eventQueue.publish(new Event("onChat", null, leaveMessage));

            // Hủy đăng ký khỏi event queue
            if (eventQueue != null) {
                EventQueues.remove(CHAT_QUEUE_NAME, EventQueues.APPLICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ListModelList<Message> getMessages() {
        return messages;
    }
}