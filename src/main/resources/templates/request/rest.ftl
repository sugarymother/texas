<div id="restUrlPrefix" hidden="hidden">${restUrl}</div>

<script type="text/javascript">
    const restUrlPrefix = $('#restUrlPrefix').text()

    const SUCCESS = 0
    const ERROR = 1
    const NOT_SINGED = 101
    const NOT_CONNECTED = 102

    function refreshRequest(callback, errCallback) {
        $.ajax({
            type: 'POST',
            url: restUrlPrefix + 'user/refresh',
            contentType: 'application/json',
            dataType: 'json',
            data: {},
            xhrFields: {
                withCredentials: true
            },
            success: function(response) {
                callback(response)
            },
            error: function(e){
                console.error(e)
                errCallback(e)
            }
        })
    }

    function signRequest(username, callback, errCallback) {
        $.ajax({
            type: 'POST',
            url: restUrlPrefix + 'user/sign',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                "username": username
            }),
            xhrFields: {
                withCredentials: true
            },
            success: function(response) {
                callback(response)
            },
            error: function(e){
                console.error(e)
                errCallback(e)
            }
        })
    }

    function getAllUserRequest(callback, errCallback) {
        $.ajax({
            type: 'GET',
            url: restUrlPrefix + 'user/list',
            contentType: 'application/json',
            dataType: 'json',
            data: {},
            xhrFields: {
                withCredentials: true
            },
            success: function(response) {
                callback(response)
            },
            error: function(e){
                console.error(e)
                errCallback(e)
            }
        })
    }
</script>