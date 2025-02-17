/*******************************************************************************
 *  * Copyright 2020 France Labs
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *******************************************************************************/
package com.francelabs.datafari.rest.v1_0.search;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.francelabs.datafari.aggregator.servlet.SearchAggregator;
import com.francelabs.datafari.rest.v1_0.exceptions.InternalErrorException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Search extends HttpServlet {

    /**
     * Automatically generated serial ID
     */
    private static final long serialVersionUID = -7963279533577712482L;

    @GetMapping("/rest/v1.0/search/*")
    protected void performSearch(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (request.getParameter("id") == null) {
                UUID id = UUID.randomUUID();
                request.setAttribute("id", id.toString());
            }
            SearchAggregator.doGetSearch(request, response);
        } catch (ServletException | IOException e) {
            throw new InternalErrorException("Error while performing the search request.");
        }
    }

    @PostMapping("/rest/v1.0/search/*")
    protected void stopSearch(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            SearchAggregator.doPostSearch(request, response);
        } catch (ServletException | IOException e) {
            throw new InternalErrorException("Error while stopping the search request.");
        }
    }
}