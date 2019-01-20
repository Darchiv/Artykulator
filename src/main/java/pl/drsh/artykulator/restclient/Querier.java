/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.RollbackException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author drsh
 */
@Transactional
public class Querier extends HttpServlet {
    private EntityManagerFactory entityManagerFactory;
    
    @PersistenceContext(unitName = "pl.drsh_Querier_PU")
    private EntityManager em;
    
    @Override
    public void init() {
        //this.entityManagerFactory = Persistence.createEntityManagerFactory("pl.drsh_Querier_PU");
    }
    
    @Override
    public void destroy() {
        //this.entityManagerFactory.close();
    }
    
    private String readPayload(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while((line = br.readLine()) != null){
            stringBuffer.append(line);
        }
        
        return stringBuffer.toString();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
    
        List<Query> queries = em.createNamedQuery("Query.findAll").getResultList();

        JSONArray arr = new JSONArray();

        for (Query query : queries) {
             JSONObject obj = new JSONObject();
             obj.put("url", query.getUrl());
             obj.put("method", query.getMethod());
             obj.put("params", query.getParams());

             arr.put(obj);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(arr.toString());
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String payload = readPayload(request);
        try {
            JSONObject jo = new JSONObject(payload);
        
            String url = (String) jo.get("url");
            String method = (String) jo.get("method");
            String params = (String) jo.get("params");
            
            try {
                em.persist(new Query(url, method, params));
            } catch (RollbackException e) {
                response.sendError(400);
            }
        } catch(JSONException | ClassCastException e) {
            response.sendError(400);
        }
    }
}
