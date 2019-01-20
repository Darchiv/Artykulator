/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.service;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author drsh
 */
@Stateless
@Path("howtobasic")
public class HowToBasic {
    @GET
    @Produces("text/plain")
    public String howToBasic() {
        return "API Usage.\nArticles:\n"
                + "GET /article/{id}  - return article data with `id` identifier\n"
                + "POST /article/  - create a new article. Parameters:\n"
                + "\t\"addedBy\": {\n" +
                "\t\t\"id\": `user id`\n" +
                "\t},\n" +
                "\t\"file\": `array of signed integers` (optional)\n" +
                "\t\"title\": `article title`,\n"
                + "\t\"description\": `article description` (optional)\n"
                + "PUT /article/{id}  - modify an existing article with `id`. Parameters:\n" +
                "\t\"file\": `array of signed integers` (optional)\n" +
                "\t\"title\": `article title` (optional),\n"
                + "\t\"description\": `article description` (optional)\n"
                + "\t\"revisionList\": [\n\t\t{\"userId\":\n\t\t\t{\"id\": `editing user id`}\n\t\t}\n\t]\n"
                + "DELETE /article/{id}  - delete an existing article with `id`\n"
                + "\nUsers:\n"
                + "GET /user/{id}  - return user data with `id` identifier\n"
                + "POST /user/  - create a new user. Parameters:\n"
                + "\t\"name\": `username`\n"
                + "PUT /user/{id}  - modify an existing user with `id`. Parameters:\n"
                + "\t\"name\": `username`\n"
                + "DELETE /user/{id}  - delete an existing user with `id`\n";
    }
}
