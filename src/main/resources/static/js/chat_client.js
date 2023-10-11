'use strict';


const userId = /*[[${user.getId()}]]*/ -1;
var stompClient = null;
const messageForm = document.querySelector('#messageForm');
const chatPage = document.querySelector('#chat-page');
const connectForm = document.querySelector('#connectForm');

function connect() {
  const Stomp = require("stompjs");
  const SockJS = require("sockjs-client");
  const socket = new SockJS("http://localhost:8080/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  console.log("Connected");
  chatPage.classList.remove('hidden');
  connectForm.classList.add('hidden');
  const recipientId = document.getElementById("recipient").value;
  stompClient.subscribe(`/user/${userId}/queue/messages`, onMessageReceived);
}

function onError(err) {
  console.log(err);
}

function onMessageReceived(msg) {
  const notification = JSON.parse(msg.body);
  const messagesContainer = document.getElementById("messageArea");
  const newMessage = document.createElement("li");
  newMessage.textContent = notification.senderName + ": " + notification.content;
  messagesContainer.appendChild(newMessage);
}

function startChat() {
  const recipientId = document.getElementById("recipient").value;
  connect();
}

function sendMessage() {
  const input = document.querySelector('input[name="user_input"]');
  const message = input.value.trim();
  if (message !== "") {
    const recipientId = document.getElementById("recipient").value;
    const messageObj = {
      senderId: userId,
      recipientName: recipientId,
      content: message,
    };
    stompClient.send("/app/chat", {}, JSON.stringify(messageObj));
    input.value = "";
  }
}

messageForm.addEventListener('submit', function (e) {
  e.preventDefault();
  sendMessage();
});
