<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Texas</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        .ce_shi {
            background: blue;
            color: white;
        }
    </style>
</head>

<#include "request/rest.ftl">
<#include "request/websocket.ftl">
<body>
    <div class="ce_shi">game ws</div>
</body>

<script type="text/javascript">
    $(function () {
        const ws = openWebsocketConnect()
    })
</script>

</html>