<div id="popUpMsgBox" style="
    visibility: hidden;
    z-index: -1;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    background-color: white;
    border: 1px solid black;
    padding: 20px 30px;
    font-size: 14px;
">popup msg</div>

<script type="text/javascript">
    let popBoxUp = false

    function popUp(msg, timeInSec) {
        let popUpBox = $('#popUpMsgBox')
        popUpBox.text(msg)
        popUpBox.css('visibility', 'visible')
        popUpBox.css('z-index', '11')
        popBoxUp = true

        if (timeInSec != null) {
            setTimeout(function () {
                popDown()
            }, timeInSec * 1000)
        }
    }

    function popDown() {
        let popUpBox = $('#popUpMsgBox')
        popUpBox.css('visibility', 'hidden')
        popUpBox.css('z-index', '-1')
        popBoxUp = false
    }
</script>
