const userId = document.getElementById('chat').getAttribute('data-user-id');
let stompClient = null;
const messageForm = document.querySelector('#messageForm');
const chatPage = document.querySelector('#chat-page');
const connectForm = document.querySelector('#connectForm');
const USER_EXISTENCE_ENDPOINT = '/api/users/accessible/';

function connect() {
  const socket = new SockJS("http://localhost:8080/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  console.log("Connected");
  chatPage.classList.remove('hidden');
  connectForm.classList.add('hidden');
  const recipientId = document.getElementById("recipient").value;

  const sortedIds = [userId, recipientId].sort();
  const chatId = sortedIds.join('_');

  stompClient.subscribe(`/user/${userId}/queue/messages/${chatId}`, onMessageReceived);
}

function onError(err) {
  console.log(err);
}

function onMessageReceived(msg) {
  const notification = JSON.parse(msg.body);
  const recipientId = document.getElementById("recipient").value;
  const sortedIds = [userId, recipientId].sort();
  const currentChatId = sortedIds.join('_');

  if (notification.chatId === currentChatId) {
    const messagesContainer = document.getElementById("messageArea");
    const newMessage = document.createElement("li");
    newMessage.textContent = notification.senderName + ": " + notification.content;
    messagesContainer.appendChild(newMessage);

    stompClient.send("/app/chat/read", {}, JSON.stringify(notification));
  }
}

function checkUserIsAccessible(userIdToCheck) {
     return fetch(`${USER_EXISTENCE_ENDPOINT}${userIdToCheck}/to/${userId}`)
        .then(response => response.json())
        .then(data => data.accessible)
        .catch(error => {
            console.error('Error while checking the user:', error);
            return false;
        });
}

function startChat() {
  const recipientId = document.getElementById("recipient").value;

  if (userId === recipientId) {
    alert("You cannot start a chat with yourself!");
    return;
  }

  checkUserIsAccessible(recipientId).then(userAccessible => {
    if (!userAccessible) {
        alert("Cannot start chatting with that user.");
        return;
    }
  });

  const sortedIds = [userId, recipientId].sort();
  const chatId = sortedIds.join('_');

  connect();
}

function sendMessage() {
  const input = document.querySelector('#message');
  const message = input.value.trim();
  if (message !== "") {
    const recipientId = document.getElementById("recipient").value;

    const messagesContainer = document.getElementById("messageArea");
    const newMessage = document.createElement("li");
    newMessage.textContent = "YOU: " + message;
    messagesContainer.appendChild(newMessage);

    const sortedIds = [userId, recipientId].sort();
    const chatId = sortedIds.join('_');

    const messageObj = {
      senderId: userId,
      recipientId: recipientId,
      content: message,
      chatId: chatId, // Добавлено поле chatId в объект сообщения
    };
    stompClient.send("/app/chat", {}, JSON.stringify(messageObj));
    input.value = "";
  }
}

messageForm.addEventListener('submit', function (e) {
  e.preventDefault();
  sendMessage();
});
