const userId = document.getElementById('chat').getAttribute('data-user-id');
const messageForm = document.querySelector('#messageForm');
const chatPage = document.querySelector('#chat-page');
const connectForm = document.querySelector('#connectForm');
const USER_EXISTENCE_ENDPOINT = "/public/validation/users/";
let recipientId = null;
let stompClient = null;

function connect() {
  const socket = new SockJS("http://localhost:8080/ws");
  stompClient = Stomp.over(socket);

  if (recipientId === null || recipientId === undefined) {
      onError("Cannot proceed connecting since the recipient id is null.");
      return;
  }

  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  console.log("Connected");
  chatPage.classList.remove('hidden');
  connectForm.classList.add('hidden');

  if (recipientId === null || recipientId === undefined) {
        onError("Cannot proceed connecting since the recipient id is null.");
        return;
  }

  const sortedIds = [userId, recipientId].sort();
  const chatId = sortedIds.join('_');

  stompClient.subscribe(`/user/${userId}/queue/messages/${chatId}`, onMessageReceived);
}

function onError(err) {
  console.log(err);
}

function onMessageReceived(msg) {
  const notification = JSON.parse(msg.body);

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

async function checkUserIsAccessible(userIdToCheck) {
  try {
    const response = await fetch(`${USER_EXISTENCE_ENDPOINT}accessible/${userIdToCheck}/to/${userId}`);
    const data = await response.json();
    return data.accessible || false;
  } catch (error) {
    console.error('Error while checking the user:', error);
    return false;
  }
}

async function fetchRecipientId(username) {
  try {
    const response = await fetch(`${USER_EXISTENCE_ENDPOINT}${username}`);
    const data = await response.json();

    if (data !== null && response.ok) {
      recipientId = data;
      return recipientId;
    } else {
      console.error(`User with username ${username} not found`);
      return null;
    }
  } catch (error) {
    console.error('Error while fetching recipientId:', error);
    return null;
  }
}

async function startChat() {
  const recipient = document.getElementById("recipient").value;
  recipientId = await fetchRecipientId(recipient);

  if (recipientId === null) {
      alert(`Cannot find user: ${recipient}`);
      return;
    }

  if (userId == recipientId) {
    alert("You cannot start a chat with yourself!");
    return;
  }

  const userAccessible = await checkUserIsAccessible(recipientId);

  if (!userAccessible) {
    alert("Cannot start chatting with that user (friends only).");
    return;
  }

  const sortedIds = [userId, recipientId].sort();
  const chatId = sortedIds.join('_');

  connect();
}

function sendMessage() {
  const input = document.querySelector('#message');
  const message = input.value.trim();
  if (message !== "") {
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
      chatId: chatId,
    };
    stompClient.send("/app/chat", {}, JSON.stringify(messageObj));
    input.value = "";
  }
}

messageForm.addEventListener('submit', function (e) {
  e.preventDefault();
  sendMessage();
});
