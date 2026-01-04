package com.shdev.monitoringservice.dto;

/**
 * DTO containing pagination navigation links.
 *
 * @param self     Link to current page
 * @param first    Link to first page
 * @param previous Link to previous page (null if on first page)
 * @param next     Link to next page (null if on last page)
 * @param last     Link to last page
 * @author Shailesh Halor
 */
public record LinksDto(
        String self,
        String first,
        String previous,
        String next,
        String last
) {
}

