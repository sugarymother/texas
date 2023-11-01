<div id="wsUrlPrefix" hidden="hidden">${wsUrl}</div>

<script type="text/javascript">
    const wsUrlPrefix = $('#wsUrlPrefix').text()

    const FLUSH_USER_LIST = 18

    function openWebsocketConnect(onlineSeries, callback) {
        let wsUrl = wsUrlPrefix + onlineSeries
        const ws = new WebSocket(wsUrl)

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

        return ws
    }
</script>