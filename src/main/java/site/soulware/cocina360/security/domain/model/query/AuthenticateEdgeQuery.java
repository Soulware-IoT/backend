package site.soulware.cocina360.security.domain.model.query;

/**
 * Resolve the edge device (and thus its organization) from the API key it presents.
 *
 * @param apiKey the raw {@code X-Edge-Api-Key} value, may be {@code null}/blank.
 */
public record AuthenticateEdgeQuery(String apiKey) {}
