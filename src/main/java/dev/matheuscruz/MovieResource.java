package dev.matheuscruz;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.ConfigProvider;

@Path("/movies")
// I am using specification instead framework specific annotation
public class MovieResource {

    @Inject
    MeterRegistry meterRegistry;

    @POST
    @Transactional
    public Response create(CreateMovieRequest request) {
        this.meterRegistry.counter("movies.created").increment();

        Movie movie = new Movie(request.name(), request.year());
        Movie.persist(movie);
        return Response.status(Response.Status.CREATED)
                .entity(movie)
                .build();
    }

    @GET
    public Response listAll() {
        this.meterRegistry.counter("movies.listed").increment();

        return Response.ok(Movie.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Movie movie = Movie.findById(id);
        if (movie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(movie).build();
    }

    @DELETE
    @Transactional
    public Response delete(Long id) {
        Movie movie = Movie.findById(id);
        if (movie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        movie.delete();
        return Response.noContent().build();
    }

    public record CreateMovieRequest(String name, int year) {}

}
