<div id="userSeries" hidden="hidden">${(user.onlineSeries)!''}</div>
<div id="userName" hidden="hidden">${(user.username)!''}</div>
<div id="userChips" hidden="hidden">${(user.chips)!''}</div>
<div id="userEarnedChips" hidden="hidden">${(user.earnedChips)!''}</div>
<div id="userRechargeTimes" hidden="hidden">${(user.rechargeTimes)!''}</div>

<script type="text/javascript">
    const user = {
        onlineSeries: $("#userSeries").text(),
        username: $("#userName").text(),
        chips: $("#userChips").text(),
        earnedChips: $("#userEarnedChips").text(),
        rechargeTimes: $("#userRechargeTimes").text()
    }
</script>