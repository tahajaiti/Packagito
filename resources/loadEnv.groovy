def envVars = []
new File(env.DOTENV_PATH).eachLine { line ->
    if (!line.trim().startsWith("#") && line.contains("=")) {
        def parts = line.split("=", 2)
        def varName = parts[0].trim()
        def varValue = parts[1].trim().replaceAll('"', '').replaceAll("'", "")

        envVars << "${varName}=${varValue}"
    }
}
return envVars