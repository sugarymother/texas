<style>
    #roomPage {
        position: absolute;
        display: inline-flex;
        flex-direction: row;
        z-index: -1;
        visibility: hidden;
        width: 100%;
        background-color: white;
        height: 100%;
        padding: 0;
        transform: translate(-25px, -25px);
    }
    .user_slot_area {
        flex-grow: 3;
    }
    .game_settings_area {
        flex-grow: 1;
        min-width: 400px;
    }
    .setting_acn, .setting_mlb, .setting_mb, .room_id {
        border: none;
        font-size: 16px;
    }
    .user_slot_list li {
        border: 1px solid black;
        font-size: 16px;
        width: 250px;
        margin: 5px;
    }
    .user_slot_list li div {
        border: none;
    }
    .user_slot_list {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }
</style>

<div id="roomPage">
    <div class="user_slot_area">
        <div class="room_id">room id</div>
        <ul class="user_slot_list">
            <li>
                <div class="user_name_slot"><b>name: </b><span>name</span></div>
                <div class="user_chips_slot"><b>chips: </b><span>chips</span></div>
                <div class="user_charge_slot"><b>charge times: </b><span>charge times</span></div>
            </li>
        </ul>
    </div>
    <div class="game_settings_area">
        <div class="setting_acn"><b>Access Chips Num: </b><span>access chips num</span></div>
        <div class="setting_mlb"><b>Min Large Bet: </b><span>min large bet</span></div>
        <div class="setting_mb"><b>Max Bet: </b><span>max bet</span></div>
        <button id="openGameBtn" disabled>START GAME</button>
        <button id="exitBtn">EXIT</button>
    </div>
</div>

<script type="text/javascript">
    let roomPageOn = false

    // binding event
    $("#exitBtn").on('click', function () {
        let roomPage = $("#roomPage")
        roomPage.css("z-index", "-1")
        roomPage.css('visibility', 'hidden')
        roomPageOn = false
        wsSendMsg(LEAVE_ROOM)
    })
    $("#openGameBtn").on('click', function () {
        wsSendMsg(OPEN_GAME, {roomId: $(".user_slot_area .room_id").text()})
    })

    function enterRoom() {
        let roomPage = $("#roomPage")
        roomPage.css("z-index", "10")
        roomPage.css('visibility', 'visible')
        roomPageOn = true
    }

    function flushRoomPage(roomSnapshot) {
        $(".user_slot_area .room_id").text(roomSnapshot.id)
        $(".setting_acn span").text(roomSnapshot.accessChipsNum)
        $(".setting_mlb span").text(roomSnapshot.minLargeBet)
        $(".setting_mb span").text(roomSnapshot.maxBet)
        let users = roomSnapshot.users
        let userSlotList = $(".user_slot_list")
        userSlotList.children().remove()
        for (const user of users) {
            if (user.isOwner) {
                user.username = user.username + '（owner）'
            }
            let newItem = $(
                '<li>' +
                    '<div class="user_name_slot"><b>name: </b><span>' + user.username + '</span></div>' +
                    '<div class="user_chips_slot"><b>chips: </b><span>' + user.chips + '</span></div>' +
                    '<div class="user_charge_slot"><b>charge times: </b><span>' + user.rechargeTimes + '</span></div>' +
                '</li>'
            )
            userSlotList.append(newItem)
        }
        if (users[roomSnapshot.mainUserIdx].isOwner) {
            $('#openGameBtn').attr('disabled', false)
        } else {
            $('#openGameBtn').attr('disabled', true)
        }
    }
</script>