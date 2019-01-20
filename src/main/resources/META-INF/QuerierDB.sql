/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
CREATE TABLE queries (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    url VARCHAR(256) NOT NULL,
    method VARCHAR(16) NOT NULL,
    params VARCHAR(2048)
);
