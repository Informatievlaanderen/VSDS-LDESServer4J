---
layout: default
title: Swagger Admin API
nav_order: 6
---


<html>
<head>
    <title>Swagger UI</title>
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.0.0/swagger-ui.css" >
    <script src="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.0.0/swagger-ui-bundle.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/4.0.0/swagger-ui-standalone-preset.js"></script>
</head>
<body>
<div id="swagger-ui"></div>
<script>
    window.onload = function() {
        const ui = SwaggerUIBundle({
            url: "./openapi.json",
            dom_id: '#swagger-ui',
            deepLinking: true,
            presets: [
                SwaggerUIBundle.presets.apis,
                SwaggerUIStandalonePreset
            ],
            plugins: [
                SwaggerUIBundle.plugins.DownloadUrl
            ],
            layout: "StandaloneLayout"
        })
    }
</script>
</body>
</html>