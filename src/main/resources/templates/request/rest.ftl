<div id="restUrlPrefix" hidden="hidden">${restUrl}</div>

<script type="text/javascript">
    const restUrlPrefix = $('#restUrlPrefix').text()

    const SUCCESS = 0
    const ERROR = 1
    const NOT_SINGED = 101
    const NOT_CONNECTED = 102

    function postRequest(url, data, callback, errCallback) {
        $.ajax({
            type: 'POST',
            url: restUrlPrefix + url,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
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

    function refreshRequest(callback, errCallback) {
        postRequest('user/refresh', {}, callback, errCallback)
    }

    function signRequest(username, callback, errCallback) {
        postRequest('user/sign', {"username": username}, callback, errCallback)
    }

    function rechargeRequest(callback, errCallback) {
        postRequest('user/recharge', {}, callback, errCallback)
    }

    function getAllUserRequest(callback, errCallback) {
        postRequest('user/list', {}, callback, errCallback)
    }
</script>