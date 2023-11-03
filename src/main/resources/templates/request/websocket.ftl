<div id="wsUrlPrefix" hidden="hidden">${wsUrl}</div>

<script type="text/javascript">
    const wsUrlPrefix = $('#wsUrlPrefix').text()

    // receive
    const OPERATE = 1
    const GET_GAME_SNAPSHOT = 2
    const LEAVE_AFTER_DIE = 3
    const OPEN_GAME = 4
    const ENTER_ROOM = 5
    const LEAVE_ROOM = 6
    // send
    const FLUSH_GAME_SNAPSHOT = 11
    const RECONNECT_INTO_GAME = 12
    const GAME_START = 13
    const GAME_OVER = 14
    const TURN_OVER = 15
    const IN_TURN_OPERATE = 16
    const FLUSH_USER_LIST = 17
    const FLUSH_ROOM_SNAPSHOT = 18

    let ws = null

    function openWebsocketConnect(onlineSeries, callback) {
        let wsUrl = wsUrlPrefix + onlineSeries
        ws = new WebSocket(wsUrl)

        ws.onopen = function (e) {
            console.log('connect opened.')
            callback.onopen(e)
        }

        ws.onclose = function (e) {
            console.log('connect closed, reason: ' + e.reason)
            callback.onclose(e.reason)
        }

        ws.onerror = function (e) {
            console.error(e)
            callback.onerror(e)
        }

        ws.onmessage = function (e) {
            callback.onmessage(JSON.parse(e.data))
        }

        ws.sendMsg = function (type, data) {
            ws.send(JSON.stringify({
                type: type,
                data: data
            }))
        }
    }

    function wsSendMsg(type, data) {
        if (ws !== null) {
            ws.sendMsg(type, data)
        }
    }
</script>