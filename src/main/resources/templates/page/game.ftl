<style>
    #gamePage {
        position: absolute;
        display: inline-flex;
        flex-direction: column;
        z-index: -1;
        visibility: hidden;
        width: 100%;
        background-color: white;
        height: 100%;
        padding: 0;
        margin: 0;
        transform: translate(-20px, -20px);
    }
    #gamePage div, #gamePage ul {
        margin: 0;
        padding: 0;
    }
    #operateList {
        height: 100%;
    }
    .user_area_box, #publicCardList, #operateList, .player_card_list {
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .public_area_box {
        position: absolute;
        left: 50%;
        top: 50%;
        background-color: white;
        transform: translate(-50%, -50%);
    }
    #topUserArea {
        flex: 6;
    }
    .middle_area {
        flex: 11;
        display: flex;
        flex-direction: row;
        border: none;
    }
    #leftUserArea, #rightUserArea {
        flex: 2;
        flex-direction: column;
    }
    .blank_area {
        border: none;
        position: relative;
        flex: 5;
    }
    #selfUserArea {
        flex: 6;
    }
    .operate_area_box {
        flex: 3;
    }
    .card_view {
        position: relative;
        border: 1px solid black;
        border-radius: 3px;
        text-align: center;
        width: 50px;
        height: 70px;
        line-height: 70px;
        font-size: 16px;
        margin: 5px;
    }
    .card_view div {
        border: none;
        position: absolute;
        left: 2px;
        top: 2px;
        font-size: 10px;
        line-height: 10px;
    }
    .game_info_box {
        text-align: center;
        border: none;
        border-bottom: 1px solid black;
        font-size: 14px;
    }
    #publicCardList {
        min-width: 320px;
        height: 84px;
    }
    .player_view {
        border-radius: 3px;
        font-size: 14px;
        border: 1px solid black;
        padding: 4px;
        margin: 4px 40px;
        min-height: 162px;
        min-width: 187px;
    }
    .player_view div, .player_view ul {
        border: none;
    }
    .operate_status {
        font-weight: bold;
        text-align: center;
        height: 20px;
    }
</style>

<div id="gamePage">
    <ul id="topUserArea" class="user_area_box">
    </ul>
    <div class="middle_area">
        <ul id="leftUserArea" class="user_area_box">
        </ul>
        <div class="blank_area">
            <div class="public_area_box">
                <div class="game_info_box">starting...</div>
                <ul id="publicCardList">
                </ul>
            </div>
        </div>
        <ul id="rightUserArea" class="user_area_box">
        </ul>
    </div>
    <ul id="selfUserArea" class="user_area_box">
    </ul>
    <div class="operate_area_box">
        <ul id="operateList">
        </ul>
    </div>
</div>

<script type="text/javascript">
    let gamePageOn = false

    let operateList = $('#operateList')
    let publicCardList = $('#publicCardList')
    let gameInfoBox = $('.game_info_box')

    let gameSnapshot = null
    let gameStart = false
    let gameOver = false
    let turnOver = false
    let playerAreaList = []


    function enterGame() {
        let gamePage = $('#gamePage')
        gamePage.css('z-index', 10)
        gamePage.css('visibility', 'visible')
        gamePageOn = true
    }

    function leaveGame() {
        let gamePage = $('#gamePage')
        gamePage.css('z-index', -1)
        gamePage.css('visibility', 'hidden')
        gamePageOn = false
    }

    // flush game snapshot
    function flushGame(snapshot, isTurnOver) {
        if (!gameStart) {
            gameStart = true
            popUp('GAME START!', 2)
        }
        if (isTurnOver) {
            turnOver = true
        } else if (turnOver) {
            turnOver = false
            popUp('NEW TURN START!', 2)
        }
        gameSnapshot = snapshot
        gameInfoBox.text(snapshot.round)
        publicCardList.children().remove()
        for(const card of snapshot.publicCards) {
            publicCardList.append(createCard(card))
        }
        playerAreaList = []
        for (let i = 0; i < snapshot.players.length; i++) {
            playerAreaList.push(
                createPlayerArea(i + 1, snapshot.players[i], turnOver, snapshot.currentPlayerIdx === i)
            )
        }

        let tempPlayerList
        if (snapshot.mainPlayerIdx === 0) {
            tempPlayerList = Array.from(playerAreaList)
        } else {
            tempPlayerList = playerAreaList.slice(snapshot.mainPlayerIdx)
                .concat(playerAreaList.slice(0, snapshot.mainPlayerIdx))

        }
        let bottom = $('#selfUserArea')
        let right  = $('#rightUserArea')
        let top    = $('#topUserArea')
        let left   = $('#leftUserArea')
        bottom.children().remove()
        right.children().remove()
        top.children().remove()
        left.children().remove()
        switch (playerAreaList.length) {
            case 2:
                bottom.append(tempPlayerList[0])
                top.append(tempPlayerList[1])
                break
            case 3:
                bottom.append(tempPlayerList[0])
                top.append(tempPlayerList[1])
                top.append(tempPlayerList[2])
                break
            case 4:
                bottom.append(tempPlayerList[0])
                left.append(tempPlayerList[1])
                top.append(tempPlayerList[2])
                right.append(tempPlayerList[3])
                break
            case 5:
                bottom.append(tempPlayerList[0])
                left.append(tempPlayerList[1])
                top.append(tempPlayerList[2])
                top.append(tempPlayerList[3])
                right.append(tempPlayerList[4])
                break
            case 6:
                bottom.append(tempPlayerList[0])
                left.append(tempPlayerList[1])
                top.append(tempPlayerList[2])
                top.append(tempPlayerList[3])
                top.append(tempPlayerList[4])
                right.append(tempPlayerList[5])
                break
            case 7:
                bottom.append(tempPlayerList[0])
                left.append(tempPlayerList[2])
                left.append(tempPlayerList[1])
                top.append(tempPlayerList[3])
                top.append(tempPlayerList[4])
                right.append(tempPlayerList[5])
                right.append(tempPlayerList[6])
                break
            case 8:
                bottom.append(tempPlayerList[0])
                left.append(tempPlayerList[2])
                left.append(tempPlayerList[1])
                top.append(tempPlayerList[3])
                top.append(tempPlayerList[4])
                top.append(tempPlayerList[5])
                right.append(tempPlayerList[6])
                right.append(tempPlayerList[7])
                break
        }
    }

    // flush game operate
    function flushGameOperate(operate) {

    }

    // create card item
    function createCard(card) {
        let number = card.number
        let suit = card.suit
        let centerSuit = card.suit
        if (number === '?') {
            number = ''
            suit = ''
        }
        let cardItem = $(
            '<li class="card_view">' +
                '<div>' + number + '<br>' + suit + '</div>' +
                '<b>' + centerSuit + '</b>' +
            '</li>')

        if (suit === '♥' || suit === '♦') {
            cardItem.css('color', 'red')
        }
        return cardItem
    }
    // create player area item
    function createPlayerArea(index, playerArea, turnOver, isOperating) {
        let username = playerArea.username
        let chips = 0
        let bet = 0
        let operateStatus = ''
        if (playerArea.leave) {
            username = username + '(left)'
        } else if (!playerArea.alive) {
            username = username + '(out)'
        } else {
            if (playerArea.disconnected) {
                username = username + '(disconnected)'
            }
            chips = playerArea.chips
            bet = playerArea.bet
            if (turnOver) {
                let finalCardType = playerArea.finalCardType
                if (finalCardType === undefined || finalCardType === null) {
                    finalCardType = {type: ''}
                }
                if (playerArea.fold) {
                    operateStatus = 'FOLD'
                } else if (playerArea.win) {
                    operateStatus = finalCardType.type + '(WIN!)'
                } else {
                    operateStatus = finalCardType.type + '(LOSE!)'
                }
            } else {
                if (isOperating) {
                    operateStatus = 'OPERATING...'
                } else if (playerArea.allin) {
                    operateStatus = 'ALLIN'
                } else if (playerArea.fold) {
                    operateStatus = 'FOLD'
                } else {
                    if (playerArea.lastOperate === undefined || playerArea.lastOperate === null) {
                        operateStatus = '...'
                    } else {
                        operateStatus = playerArea.lastOperate
                    }
                }
            }
        }

        let playerAreaItem = $(
            '<li class="player_view">' +
                '<div><b>' + index + '</b>. <span>' + username + '</span></div>' +
                '<div>&nbsp;&nbsp;&nbsp;&nbsp;<b>CHIPS: </b><span>' + chips + '</span></div>' +
                '<div>&nbsp;&nbsp;&nbsp;&nbsp;<b>BET&nbsp;&nbsp;&nbsp;: </b><span>' + bet + '</span></div>' +
                '<div class="operate_status">' + operateStatus + '</div>' +
                '<ul class="player_card_list">' +
                '</ul>' +
            '</li>')

        if (playerArea.alive) {
            let hands = playerAreaItem.children('ul')
            hands.append(createCard(playerArea.hand1))
            hands.append(createCard(playerArea.hand2))
        }

        return playerAreaItem
    }

    $(function () {
        function test() {
            enterGame()
            let snapshot = {
                players: [],
                publicCards: [],
                round: "river round",
                currentPlayerIdx: 1,
                mainPlayerIdx: 1
            }
            for (let i = 0; i < 3; i++) {
                snapshot.publicCards[i] = {
                    number: '?',
                    suit: '?'
                }
            }
            for (let i = 0; i < 8; i++) {
                snapshot.players[i] = {
                    username: "moyujian" + i,
                    chips: 320,
                    bet: 40,
                    hand1: {
                        number: '?',
                        suit: '?'
                    },
                    hand2: {
                        number: '?',
                        suit: '?'
                    },
                    alive: true,
                    disconnected: false,
                    leave: false,
                    win: false,
                    fold: false,
                    allin: false,
                    lastOperate: null,
                    finalCardType: null
                }
            }
            flushGame(snapshot, false)
        }
        // test()
    })
</script>
