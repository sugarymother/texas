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
<#include "request/rest.ftl">
<#include "request/websocket.ftl">
<body>
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
        $('#testBtn').on('click', function (e) {
            window.location.href='./game'
        })

        let user
        function init(userData) {
            user = userData
            $('#selfUser .username').text(user.username)
            $('#selfUser .coin').text(user.chips)
            $('#selfUser .earned').text(user.earnedChips)
            $('#selfUser .recharge_times').text(user.rechargeTimes)

            // open connection
            onlineSeries = userData.onlineSeries
            const ws = openWebsocketConnect({
                onopen: function () {},
                onclose: function (reason) {
                    alert('connection closed, reason: ' + reason)
                    location.reload()
                },
                onmessage: function (data) {
                    if (data.type === FLUSH_USER_LIST) {
                        refreshUserList()
                    }
                }
            })
        }

        function refreshUserList() {
            let userList = $('.user_list')
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
    })
</script>

</html>