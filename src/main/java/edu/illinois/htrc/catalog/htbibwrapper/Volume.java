package edu.illinois.htrc.catalog.htbibwrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("vol")
public class Volume {

    private final String endpoint = "http://catalog.hathitrust.org/api/volumes/full/htid/";

    @GET
    @Path("{volumeId}")
    public String sayHello(@PathParam("volumeId") String volumeId) {
        StringBuilder stringBuilder = new StringBuilder("HT Bib Wrapper | Hello ");
        stringBuilder.append(volumeId).append("!");

        return stringBuilder.toString();
    }

}
