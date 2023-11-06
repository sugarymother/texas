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
        }
        body div {
            margin: 5px;
            padding: 5px;
            border: 1px solid black;
        }
        #startBtn, #rechargeBtn {
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
            <div id="startBtn">START GAME</div>
            <div id="rechargeBtn">RECHARGE COIN</div>
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
                    if (data.type === FLUSH_USER_LIST) {
                        refreshUserList()
                    } else if (data.type === FLUSH_ROOM_SNAPSHOT) {
                        if (!roomPageOn) {
                            enterRoom()
                            if (popBoxUp) {
                                popDown()
                            }
                        }
                        flushRoomPage(data.data)
                    } else if (data.type === START_GAME_FAILED) {
                        popUp(data.data, 2)
                    } else if (data.type === GAME_START) {
                        refreshSelfUserInfo()
                        // TODO start game
                    }
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