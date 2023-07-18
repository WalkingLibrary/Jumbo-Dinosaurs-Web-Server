package com.jumbodinosaurs.webserver.netty.handler.http.util.queryparameters;

import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;

import java.util.HashMap;

public class QueryParameters
{
    public HashMap<String, String> parameters = new HashMap<>();

    private QueryParameters(HashMap<String, String> parameters)
    {
        this.parameters = parameters;
    }


    public String getValue(String key)
    {
        return parameters.get(key);
    }


    public static QueryParameters parse(HTTPMessage message)
    {
        return new QueryParameters(parseQueryParams(message.getPath()));
    }

    private static HashMap<String, String> parseQueryParams(String requestedPath)
    {
        HashMap<String, String> queryParams = new HashMap<>();

        // Check if the requested path contains query parameters
        int queryStartIndex = requestedPath.indexOf('?');
        if (queryStartIndex >= 0 && queryStartIndex < requestedPath.length() - 1)
        {
            // Extract the query string from the requested path
            String queryString = requestedPath.substring(queryStartIndex + 1);

            // Split the query string into individual parameters
            String[] params = queryString.split("&");
            for (String param : params)
            {
                // Split the parameter into key-value pair
                String[] keyValue = param.split("=");
                if (keyValue.length == 2)
                {
                    // Store the key-value pair in the HashMap
                    String key = keyValue[0];
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }
        }

        return queryParams;
    }
}
