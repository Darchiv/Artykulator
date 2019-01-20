/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.restclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author drsh
 */
public class Querier extends HttpServlet {
    private EntityManagerFactory entityManagerFactory;
    
    @Override
    public void init() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("pl.drsh_Querier_PU");
    }
    
    @Override
    public void destroy() {
        this.entityManagerFactory.close();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
    
        List<Query> queries = this.entityManagerFactory.createEntityManager().createNamedQuery("Query.findAll").getResultList();

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
}
