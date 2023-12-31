<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Texas</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        body {
            padding: 10px;
            height: 100%;
            font-size: 18px;
            background-color: white;
            zoom: 0.8;
        }
        body div {
            margin: 5px;
            padding: 5px;
            border: 1px solid black;
        }
        button {
            display: inline-block;
            padding: 5px;
            font-size: 14px;
            cursor: pointer;
        }
        .border_box {
            display: flex;
            flex-direction: row;
            width: auto;
            height: auto;
            border: none;
        }
        .left_area {
            flex-grow: 3;
            text-align: center;
        }
        .user_list_area {
            flex-grow: 1;
        }
        .user_box {
            text-align: left;
        }
        .user_box div {
            border: none;
            display: inline;
        }
        .online_status {
            color: green;
        }
        li, ul {
            list-style: none;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<#include "request/user.ftl">
<#include "request/rest.ftl">
<#include "request/websocket.ftl">
<body>
    <#include "page/popup.ftl">
    <#include "page/room.ftl">
    <#include "page/game.ftl">
    <div class="border_box">
        <div class="left_area">
            <div id="selfUser" class="user_box">
                <b>name: </b><div class="username">username</div>
                <div class="online_status">online</div>
                <br>
                <b>coin: </b><div class="coin">coin</div>
                <br>
                <b>earned: </b><div class="earned">earned</div>
                <br>
                <b>recharge times: </b><div class="recharge_times">times</div>
            </div>
            <br>
            <button id="startBtn">START GAME</button>
            <button id="rechargeBtn">RECHARGE COIN</button>
        </div>
        <div class="user_list_area">
            <b class="title">user list</b>
            <ul class="user_list"></ul>
        </div>
    </div>
</body>

<script type="text/javascript">
    $(function () {
        // binding event
        $('#rechargeBtn').on('click', function (e) {
            if (confirm("kneel down and beg moyujian to give you more coins?")) {
                rechargeRequest(function (resp) {
                    setSelfUser(resp.data)
                })
            }
        })
        $('#startBtn').on('click', function () {
            wsSendMsg(ENTER_ROOM, null)
            popUp("finding room...")
        })

        // websocket msg handler
        function handleMsg(msg) {
            switch (msg.type) {
                case FLUSH_USER_LIST:
                    if (!gamePageOn) {
                        refreshUserList()
                    }
                    break

                case FLUSH_ROOM_SNAPSHOT:
                    if (!roomPageOn) {
                        enterRoom()
                        if (popBoxUp) {
                            popDown()
                        }
                    }
                    flushRoomPage(msg.data)
                    break

                case START_GAME_FAILED:
                    if (roomPageOn) {
                        popUp(msg.data, 2)
                    }
                    break

                case GAME_START:
                    refreshSelfUserInfo()
                    if (roomPageOn) {
                        closeRoom()
                    }
                    popDown()
                    enterGame()
                    break
                case FLUSH_GAME_SNAPSHOT:
                    flushGame(msg.data, false)
                    break
                case RECONNECT_INTO_GAME:
                    if (roomPageOn === false && gamePageOn === false) {
                        enterGame()
                        wsSendMsg(GET_GAME_SNAPSHOT)
                    }
                    break
                case TURN_OVER:
                    flushGame(msg.data, true)
                    popUp('TURN OVER', 2)
                    break
                case IN_TURN_OPERATE:
                    flushGameOperate(msg.data)
                    break
                case GAME_OVER:
                    flushGame(msg.data, true, true)
                    refreshSelfUserInfo()
                    setTimeout(function () {
                        leaveGame()
                    }, 7 * 1000)
                    break
            }
        }

        function init(userData) {
            setSelfUser(userData)

            // open connection
            openWebsocketConnect(userData.onlineSeries, {
                onopen: function () {},
                onclose: function (reason) {
                    alert('connection closed, reason: ' + reason)
                    location.reload()
                },
                onmessage: function (data) {
                    console.log(data)
                    handleMsg(data)
                }
            })
        }

        function setSelfUser(userData) {
            $('#selfUser .username').text(userData.username)
            $('#selfUser .coin').text(userData.chips)
            $('#selfUser .earned').text(userData.earnedChips)
            $('#selfUser .recharge_times').text(userData.rechargeTimes)
        }

        function refreshUserList() {
            let userList = $('.user_list')
            userList.children().remove()
            getAllUserRequest(function (resp) {
                for (const userData of resp.data) {
                    let newItem = $(
                        '<div class="user_box">' +
                        '<b>name: </b><div class="username">' + userData.username + '</div>' +
                        '<div class="online_status">' + userData.status + '</div>' +
                        '<br>' +
                        '<b>coin: </b><div class="coin">' + userData.chips + '</div>' +
                        '<br>' +
                        '<b>earned: </b><div class="earned">' + userData.earnedChips + '</div>' +
                        '<br>' +
                        '<b>recharge times: </b><div class="recharge_times">' + userData.rechargeTimes + '</div>' +
                        '</div>')
                    userList.append(newItem)
                    if (userData.status === 'disconnected' || userData.status === 'offline') {
                        newItem.find('.online_status').css('color', 'grey')
                    }
                }
            })
        }

        function refreshSelfUserInfo() {
            refreshRequest(function (resp) {
                if (resp.status === SUCCESS) {
                    setSelfUser(resp.data)
                }
            })
        }

        // init
        if (user.onlineSeries !== '') {
            init(user)
        } else {
            refreshRequest(function (resp) {
                if (resp.status === SUCCESS) {
                    init(resp.data)
                } else if (resp.status === NOT_SINGED) {
                    let username = prompt("input your username to create a new account:")
                    signRequest(username, function (signResp) {
                        init(signResp.data)
                    })
                }
            })
        }
    })
</script>

</html>