package es.iesjandula.reaktor_projector_server.parsers.interfaces;

import java.util.Scanner;

import es.iesjandula.reaktor_projector_server.utils.ProjectorServerException;

/**
 * This interface defines the contract for parsing Projector Commands from a given input source.
 * Implementations of this interface should process commands from a {@link Scanner} and 
 * handle any necessary persistence or validation.
 * 
 * <p>The parser reads Commands data, processes it, and may store it in a database or 
 * another data structure. If an error occurs during processing, a 
 * {@link ProjectorServerException} is thrown.</p>
 * 
 * @author David Jason Gianmoena (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.0
 */
public interface ICommandParser
{
    /**
     * Parses Projector Commands data from the provided {@link Scanner} input.
     * 
     * @param scanner The {@link Scanner} containing the Commands data to parse.
     * @throws ProjectorServerException If an error occurs during parsing or processing.
     */
    public String parseCommands(Scanner scanner) throws ProjectorServerException;
}
