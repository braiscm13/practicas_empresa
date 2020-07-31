package com.opentach.client.util.printer;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.opentach.client.util.TGDFileInfo;

/**
 * Para imprimir tarjetas de TC y/o datos de Ficheros
 *
 * @author nobody
 */
public final class TicketPrinter {

	private static final Logger		logger		= LoggerFactory.getLogger(TicketPrinter.class);

	private static final Font		FTITLE		= new Font("Dialog", Font.BOLD, 10);
	private static final Font		FSUBTITLE	= new Font("Dialog", Font.BOLD, 7);
	private static final Font		FDETAIL		= new Font("Dialog", Font.BOLD, 6);
	private static final Font		FFILENAME	= new Font("Dialog", Font.BOLD, 5);
	private static final ImageIcon	IMGTICKET	= ApplicationManager.getIcon("images/opentach-ticket.png");
	private static final Image		IMGOK		= ApplicationManager.getIcon("images/check2_10.png").getImage();
	private static final Image		IMGKO		= ApplicationManager.getIcon("images/delete2_10.png").getImage();

	public TicketPrinter() {}

	private class TicketPrintable implements Printable {

		protected List<TGDFileInfo>	lPFI;
		protected String			cdo;

		protected TicketPrintable(String cdo, List<TGDFileInfo> lPFI) {
			this.lPFI = lPFI;
			this.cdo = cdo;
		}

		@Override
		public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex > 0) {
				return Printable.NO_SUCH_PAGE;
			}
			try {
				int x = (int) pageFormat.getImageableX();
				int y = (int) pageFormat.getImageableY();
				int w = (int) pageFormat.getImageableWidth();
				int h = (int) pageFormat.getImageableHeight();
				Graphics2D g2d = (Graphics2D) g;
				g2d.setClip(x, y, w, h);
				//
				x = w / 8;
				// TITULO
				int ystartline = y + 10;
				int yincritem = 10;
				int yincrdetail = 10;
				int xstartline = 2;// 30 para ancho de 80mm; //10 para 50 mm
				String filenamedscr = ApplicationManager.getTranslation("ticket.filenamedscr");
				String namedescr = ApplicationManager.getTranslation("ticket.namedescr");
				String datedescr = ApplicationManager.getTranslation("ticket.datedescr");
				int iconheight = (int) (((w / 3) * (double) TicketPrinter.IMGTICKET.getIconHeight()) / TicketPrinter.IMGTICKET.getIconWidth());

				g2d.drawImage(TicketPrinter.IMGTICKET.getImage(), w / 3, ystartline, w / 3, iconheight, null);
				ystartline += iconheight + yincrdetail;
				// SUBTITULO
				FontMetrics fm = null;
				int titlew = 0;
				int titleh = 0;
				if (this.cdo != null) {
					g2d.setFont(TicketPrinter.FSUBTITLE);
					fm = g2d.getFontMetrics();
					titlew = fm.stringWidth(this.cdo);
					titleh = fm.getHeight();
					ystartline += titleh;
					// limitado a 30 caracteres
					if (this.cdo.length() > 30) {
						this.cdo = this.cdo.substring(0, this.cdo.length() > 30 ? 30 : this.cdo.length());
					}
					g2d.drawString(this.cdo, (w - titlew) / 2, ystartline);
				}
				// ESTADO
				ystartline += yincrdetail;
				g2d.setFont(TicketPrinter.FTITLE);
				fm = g2d.getFontMetrics();
				boolean success = TGDFileInfo.isSuccess(this.lPFI);
				String status = success ? ApplicationManager.getTranslation("ticket.OK") : ApplicationManager.getTranslation("ticket.KO");
				titlew = fm.stringWidth(status);
				titleh = fm.getHeight();
				if (this.lPFI.size() == 1) {
					ystartline += titleh;
					g2d.drawString(status, (w - titlew) / 2, ystartline);
				}
				// DATOS
				ystartline += yincrdetail + yincritem;
				g2d.setFont(TicketPrinter.FDETAIL);
				int xitems = (w / 4) - 5; // w/4
				for (int i = 0; i < this.lPFI.size(); i++) {
					TGDFileInfo ufi = this.lPFI.get(i);
					g2d.drawString(filenamedscr, x + (xstartline / 2), ystartline);
					String filename = ufi.getOrigFile().getName();
					if (filename.length() > 45) {
						filename = filename.substring(0, 42) + "...";
					}
					ystartline += yincritem;
					g2d.setFont(TicketPrinter.FFILENAME);
					g2d.drawString(filename, xitems, ystartline);
					g2d.setFont(TicketPrinter.FDETAIL);
					// String ok = null;
					if (ufi.isSuccess()) {
						g2d.drawImage(TicketPrinter.IMGOK, w - (w / 8) - 5, (ystartline + yincritem) - TicketPrinter.IMGKO.getHeight(null), null);
					} else {
						g2d.drawImage(TicketPrinter.IMGKO, w - (w / 8) - 5, (ystartline + yincritem) - TicketPrinter.IMGKO.getHeight(null), null);
					}
					ystartline += yincritem;
					String name = ufi.getOwnerName();
					g2d.drawString(namedescr, x + (xstartline / 2), ystartline);
					if (name == null) {
						name = ApplicationManager.getTranslation("ticket.unknown");
					}
					ystartline += yincritem;
					g2d.drawString(name, xitems, ystartline);
					ystartline += yincrdetail + yincritem;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss dd/MM/yyyy");
				Date df = null;// TachoFileUtils.getDateFromName(dcf);
				if (df == null) {
					df = new Date();
				}
				String date = sdf.format(df);
				g2d.drawString(datedescr, x + (xstartline / 2), ystartline);
				// ystartline += yincritem;
				int xdate = w - g2d.getFontMetrics().stringWidth(date) - 15;
				g2d.drawString(date, xdate, ystartline);

				// PRÓXIMA FECHA DE DESCARGA
				ystartline += yincritem;
				Calendar instance = Calendar.getInstance();
				instance.setTime(df);
				instance.add(Calendar.DAY_OF_MONTH, 28);
				df = instance.getTime();
				date = sdf.format(df);
				datedescr = ApplicationManager.getTranslation("ticket.datedescrnext");
				g2d.drawString(datedescr, x + (xstartline / 2), ystartline);
				// ystartline += yincritem;
				xdate = w - g2d.getFontMetrics().stringWidth(date) - 15;
				g2d.drawString(date, xdate, ystartline);

				// FIRMA
				ystartline += yincrdetail + yincritem;
				String firma = ApplicationManager.getTranslation("ticket.firma");
				g2d.drawString(firma, x + (xstartline / 2), ystartline);
				ystartline += yincrdetail;
				BasicStroke bs = new BasicStroke(2f);
				g2d.setStroke(bs);
				// g2d.drawRect(x + xstartline/2, ystartline, w - xstartline
				// -10, 50);
				g2d.drawRect(w / 8, ystartline, (6 * w) / 8, 50);
				return Printable.PAGE_EXISTS;
			} catch (Exception ex) {
				TicketPrinter.logger.error(null, ex);
				return Printable.NO_SUCH_PAGE;
			}
		}
	}

	private final int computeHeight(final List<TGDFileInfo> lPFI) {
		final int lineheight = lPFI == null ? 0 : lPFI.size();
		final int h = 119 + (lineheight * 30) + 80 + 10 + (lineheight * 20);
		return h;
	}

	public void print(String cdo, List<TGDFileInfo> lPFI) {
		PrinterJob job = PrinterJob.getPrinterJob();
		Book book = new Book();
		PageFormat jpf = job.defaultPage();
		if (jpf != null) {
			TicketPrinter.logger.info("PRINTER DEFAULT=> Printing in {} x {} pixels.", jpf.getWidth(), jpf.getHeight());
		}
		Paper p = new Paper();
		double height = this.computeHeight(lPFI);
		double width = 164.4; // 164.4 => 58 mm 216 => 80mm
		p.setSize(width, height); // (216, height);
		p.setImageableArea(0, 0, width, height);
		// A4 height
		PageFormat dpf = new PageFormat();
		dpf.setPaper(p);
		dpf.setOrientation(PageFormat.PORTRAIT);
		TicketPrinter.logger.info("PRINTER CONFIGURED=> Printing in {} x {} pixels.", dpf.getWidth(), dpf.getHeight());
		TicketPrinter.logger.info("PRINTER CONFIGURED=> Area {}, {}, {}, 3}.", dpf.getImageableX(), dpf.getImageableY(), dpf.getImageableWidth(),
				dpf.getImageableHeight());
		book.append(new TicketPrintable(cdo, lPFI), dpf);
		job.setPageable(book);
		try {
			job.print();
		} catch (PrinterException ex) {
			TicketPrinter.logger.error(null, ex);
		}
	}

}
