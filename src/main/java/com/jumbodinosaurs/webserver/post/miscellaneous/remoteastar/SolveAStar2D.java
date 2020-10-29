package com.jumbodinosaurs.webserver.post.miscellaneous.remoteastar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.devlib.pathfinding.Path;
import com.jumbodinosaurs.devlib.pathfinding.astar.AStarNode;
import com.jumbodinosaurs.devlib.pathfinding.astar.AStarPathBuilder;
import com.jumbodinosaurs.devlib.pathfinding.astar.TwoDIntArrayAStarMap;
import com.jumbodinosaurs.devlib.pathfinding.exceptions.NoAvailablePathException;
import com.jumbodinosaurs.devlib.pathfinding.exceptions.PreMatureStopException;
import com.jumbodinosaurs.devlib.util.objects.Point2D;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.ResponseHeaderUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;

import java.util.ArrayList;

public class SolveAStar2D extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for solving a 2D AStar Int Array Map
         *  Check/Verify PostRequest Attributes
         *  Solve the Map given
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        // Check/Verify PostRequest Attributes
        
        //The AStar2DIntArrayMap should be stored in the content attribute
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        String content = request.getContent();
        AStar2DIntArrayMap aStar2DIntArrayMap;
        try
        {
            aStar2DIntArrayMap = new Gson().fromJson(content, AStar2DIntArrayMap.class);
        }
        catch(JsonParseException e)
        {
            response.setMessage400();
            e.printStackTrace();
            return response;
        }
        
        /* Validate the
         *
         * Size
         * Shape
         * Contents
         *
         *  of the Map
         *
         */
        int[][] map = aStar2DIntArrayMap.getMap();
        boolean hasStartNode, hasGoalNode;
        hasStartNode = false;
        hasGoalNode = false;
        for(int r = 0; r < map.length; r++)
        {
    
            //Shape
            // Needs To be square for the AStarPathBuilder to work
            if(map[r].length != map.length)
            {
                response.setMessage400();
                return response;
            }
            
            for(int c = 0; c < map[r].length; c++)
            {
                //Size
                if(c > 15 || r > 15)
                {
                    response.setMessage400();
                    return response;
                }
    
                /* Map Makers
                 * 0 means the cell can be traversed
                 * 1 means the cell can not be traversed
                 * 2 is the start cell
                 * 3 is the goal cell
                 * 4 is a path Node
                 *  */
                //Contents
                if(map[r][c] == 2)
                {
                    //400 for maps with multiple start nodes
                    if(hasStartNode)
                    {
                        response.setMessage400();
                        return response;
                    }
                    hasStartNode = true;
                }
                
                //Contents
                if(map[r][c] == 3)
                {
                    //400 for maps with multiple goal nodes
                    if(hasGoalNode)
                    {
                        response.setMessage400();
                        return response;
                    }
                    hasGoalNode = true;
                }
    
                //Can't have path nodes in the map to solve
                if(map[r][c] == 4)
                {
                    response.setMessage400();
                    return response;
                }
            }
        }
        
        //can't solve for the path without the start and goal nodes
        if(!hasGoalNode || !hasStartNode)
        {
            response.setMessage400();
            return response;
        }
    
        Point2D startPoint = TwoDIntArrayAStarMap.getStartPoint(map);
        Point2D goalPoint = TwoDIntArrayAStarMap.getGoalPoint(map);
        AStarNode startNode = new AStarNode(null, startPoint, 0);
        AStarNode goalNode = new AStarNode(null, goalPoint, Double.MAX_VALUE);
    
        TwoDIntArrayAStarMap map2D = new TwoDIntArrayAStarMap(startNode, goalNode, map);
        AStarPathBuilder pathBuilder = new AStarPathBuilder(map2D)
        {
            @Override
            public void buildingLoopHookStart()
            {
            
            }
        
            @Override
            public void buildingLoopHookMiddle()
            {
            
            }
        
            @Override
            public void buildingLoopHookEnd()
            {
    
            }
        };
        
        Path path;
        try
        {
            path = pathBuilder.buildPath();
        }
        catch(NoAvailablePathException e)
        {
    
            JsonObject object = new JsonObject();
            object.addProperty("failureReason", "NoPath");
            String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
            response.setMessage200(jsonApplicationTypeHeader, object.toString());
            return response;
        }
        catch(PreMatureStopException e)
        {
            response.setMessage500();
            return response;
        }
    
        /* Map Makers
         * 0 means the cell can be traversed
         * 1 means the cell can not be traversed
         * 2 is the start cell
         * 3 is the goal cell
         * 4 is a path Node
         *  */
        for(Point2D point : (ArrayList<Point2D>) path.getPath())
        {
            int pointX, pointZ;
            pointX = (int) point.getX();
            pointZ = (int) point.getZ();
            map[pointX][pointZ] = 4;
        }
        JsonObject object = new JsonObject();
        object.addProperty("solvedMap", new Gson().toJson(aStar2DIntArrayMap));
        String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
        response.setMessage200(jsonApplicationTypeHeader, object.toString());
        return response;
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresPasswordAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
