/*
 * This sample java program uses jPDFWriter
 * to open a text file and create a PDF file
 * from the text contained in the text file.
 * It takes care of pagination.
 *
 */

import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;


import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFGraphics;
import com.qoppa.pdfWriter.PDFPage;

public class CompetitionPDFCreator
{
    public static void generatePDF (Map<String, List<String>> matchList)
    {
        // Page dimensions and margins, in inches
        float pageWidth = 8.5f;
        float pageHeight = 11f;

        float marginLeft = 1;
        float marginTop = 1;


        // Define page format for PDF document
        Paper p = new Paper ();
        // set paper size
        p.setSize(pageWidth * 72, pageHeight * 72);
        // set no margin to paper (we're taking care of the margins when writing)
        p.setImageableArea(0, 0, pageWidth * 72,  pageHeight * 72);
    	PageFormat pageFormat = new PageFormat ();
    	pageFormat.setPaper(p);


        try
        {
            // Create the PDF document
            PDFDocument pdfDoc = new PDFDocument();

            // Create font
            Font font = PDFGraphics.COURIER.deriveFont(Font.PLAIN, 18f);

            // Init page information
            PDFPage newPage = null;
            Graphics2D g2 = null;
            FontMetrics fm = null;
            float currentY = marginTop * 72;

            // Create a reader to read the input text file (uses the default encoding)
            // !! This assumes that the text file is using the default OS encoding
            // otherwise you will need to specify the encoding

            int count = 0;
            for (String matchNum : matchList.keySet())
            {
                // Create new page when needed
                if (count == 0)
                {
                    newPage = pdfDoc.createPage(pageFormat);
                    pdfDoc.addPage(newPage);
                    g2 = newPage.createGraphics();
                    g2.setFont(font);
                    fm = g2.getFontMetrics();
                    currentY = marginTop * 72;
                }
                else
                {	currentY = (marginTop * 72) + (fm.getHeight() * 23);		}

                // Draw the line
                g2.drawString("Match " + matchNum, marginLeft * 72, currentY);

                g2.drawString("                Game:____________________", marginLeft * 72, currentY);

                // Advance to next line
                currentY += fm.getHeight();

                for (String name : matchList.get(matchNum))
                {
                	currentY += fm.getHeight();
                	g2.drawString(name, marginLeft * 72, currentY);
                	currentY += fm.getHeight();
                	g2.drawString("                _ , _ _ _ , _ _ _ , _ _ _", marginLeft * 72, currentY);
                	currentY += fm.getHeight();
                }

                count = (count + 1) % 2;
            }

            // Save the document
            pdfDoc.saveDocument("output.pdf");
        }
        catch (IOException ioE)
        {
            ioE.printStackTrace();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}