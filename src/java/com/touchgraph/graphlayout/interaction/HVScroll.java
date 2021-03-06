/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2001-2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission.  For written permission, please contact
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

package com.touchgraph.graphlayout.interaction;

import  com.touchgraph.graphlayout.*;

import  java.awt.event.*;
import  java.awt.geom.*;
import  java.awt.Point;
import  javax.swing.*;

/** HVScroll:  Allows for scrolling horizontally+vertically.  This can be
  * done in all sorts of ways, for instance by using the scrollbars, or by
  * dragging.
  *
  * <p>
  * This code is more complex then it would seem it should be, because scrolling
  * has to be independent of the screen being warped by lenses.  HVScroll needs
  * to use the tgLensSet object because the offset is recorded in real coordinates,
  * while the user interacts with the drawn coordinates.
  *
  * @author   Alexander Shapiro
  * @author   Murray Altheim (2002-09-17 thread groups, 2002-12-12 v1.22 features)
  * @version  1.21  $Id: HVScroll.java,v 1.1 2006/10/31 22:31:48 admin Exp $
  */
public class HVScroll implements GraphListener {

    private DScrollbar horizontalSB;
    private DScrollbar verticalSB;

    HVLens hvLens;
    HVDragUI hvDragUI;
    HVScrollToCenterUI hvScrollToCenterUI;
    public boolean scrolling;

    private boolean adjustmentIsInternal;
    private TGPanel tgPanel;
    private TGLensSet tgLensSet;

    Point2D.Double offset;

  // ............

   /** Constructor with a TGPanel <tt>tgp</tt> and TGLensSet <tt>tgls</tt>.
     */
    public HVScroll( TGPanel tgp, TGLensSet tgls )
    {
        tgPanel=tgp;
        tgLensSet=tgls;

        offset=new Point2D.Double(0,0);
        scrolling = false;
        adjustmentIsInternal = false;

        horizontalSB = new DScrollbar(JScrollBar.HORIZONTAL, 0, 100, -1000, 1100);
        horizontalSB.setBlockIncrement(100);
        horizontalSB.setUnitIncrement(20);

        horizontalSB.addAdjustmentListener(new horizAdjustmentListener());

        verticalSB = new DScrollbar(JScrollBar.VERTICAL, 0, 100, -1000, 1100);
        verticalSB.setBlockIncrement(100);
        verticalSB.setUnitIncrement(20);

        verticalSB.addAdjustmentListener(new vertAdjustmentListener());

        hvLens=new HVLens();
        hvDragUI = new HVDragUI(); //Hopefully this approach won't eat too much memory
        hvScrollToCenterUI = new HVScrollToCenterUI();

        tgPanel.addGraphListener(this);
    }

    public JScrollBar getHorizontalSB() { return horizontalSB; }

    public JScrollBar getVerticalSB() { return verticalSB; }

    public HVDragUI getHVDragUI() { return hvDragUI; }

    public HVLens getLens() { return hvLens; }

    public TGAbstractClickUI getHVScrollToCenterUI() { return hvScrollToCenterUI; }

    public Point2D.Double getTopLeftDraw() {
        Point2D.Double tld = tgPanel.getTopLeftDraw();
        tld.setLocation(tld.x-tgPanel.getSize().width/4,tld.y-tgPanel.getSize().height/4);
        return tld;
    }

    public Point2D.Double getBottomRightDraw() {
        Point2D.Double brd = tgPanel.getBottomRightDraw();
        brd.setLocation(brd.x+tgPanel.getSize().width/4,brd.y+tgPanel.getSize().height/4);
        return brd;
    }

    public Point2D.Double getDrawCenter() { //Should probably be called from tgPanel
        return new Point2D.Double(tgPanel.getSize().width/2,tgPanel.getSize().height/2);
    }

    Thread noRepaintThread;

    public void graphMoved() { //From GraphListener interface
        if (tgPanel.getDragNode()==null && tgPanel.getSize().height>0)    {
            Point2D.Double drawCenter = getDrawCenter();

            Point2D.Double tld = getTopLeftDraw();
            Point2D.Double brd = getBottomRightDraw();

            double newH = (-(tld.x-drawCenter.x)/(brd.x-tld.x)*2000-1000);
            double newV = (-(tld.y-drawCenter.y)/(brd.y-tld.y)*2000-1000);

            boolean beyondBorder = true;
            if ( newH < horizontalSB.getMaximum() && newH > horizontalSB.getMinimum() 
                    && newV < verticalSB.getMaximum()
                    && newV > verticalSB.getMinimum() ) beyondBorder=false;
            adjustmentIsInternal = true;
            horizontalSB.setDValue(newH);
            verticalSB.setDValue(newV);
            adjustmentIsInternal = false;
            if ( beyondBorder ) {
                adjustHOffset();
                adjustVOffset();
                tgPanel.repaint();
            }
        }
        // altheim: added from v1.22
        if ( noRepaintThread != null && noRepaintThread.isAlive() ) noRepaintThread.interrupt();
        noRepaintThread = new Thread(tgPanel.getThreadGroup(),"noRepaint") {
            public void run() {
                try {
                    Thread.currentThread().sleep(40); //Wait 40 milliseconds before repainting
                } catch ( InterruptedException ex ) {}
            }
        };
        noRepaintThread.start();
    }

    public void graphReset() { //From GraphListener interface
        horizontalSB.setDValue(0);
        verticalSB.setDValue(0);

        adjustHOffset();
        adjustVOffset();
    }

    class DScrollbar extends JScrollBar {
        private double doubleValue;

        DScrollbar(int orient, int val, int vis, int min, int max){
            super(orient, val, vis, min, max);
            doubleValue=val;
        }
        public void setValue(int v) { doubleValue = v; super.setValue(v); }
        public void setIValue(int v) { super.setValue(v); }
        public void setDValue(double v) {
            doubleValue = Math.max(getMinimum(),Math.min(getMaximum(),v));
            setIValue((int) v);
        }
        public double getDValue() { return doubleValue;}
    }

    private void adjustHOffset() { //The inverse of the "graphMoved" function.
        //System.out.println(horizontalSB.getDValue());
        for(int iterate=0;iterate<3;iterate++) { // Iteration needed to yield cerrect results despite warping lenses
            Point2D.Double center= tgPanel.getCenter();
            Point2D.Double tld = getTopLeftDraw();
            Point2D.Double brd = getBottomRightDraw();

            double newx = ((horizontalSB.getDValue()+1000.0)/2000)*(brd.x-tld.x)+tld.x;
            double newy = tgPanel.getSize().height/2;
            Point2D.Double newCenter = tgLensSet.convDrawToReal(newx,newy);
            offset.setLocation( offset.x+(newCenter.x-center.x),
                                offset.y+(newCenter.y-center.y) );
            tgPanel.processGraphMove();
        }
    }

    private void adjustVOffset() { //The inverse of the "graphMoved" function.
        for(int iterate=0;iterate<10;iterate++) { // Iteration needed to yeild cerrect results depite warping lenses
            Point2D.Double center= tgPanel.getCenter();
            Point2D.Double tld = getTopLeftDraw();
            Point2D.Double brd = getBottomRightDraw();

            double newx = tgPanel.getSize().width/2;
            double newy = ((verticalSB.getDValue()+1000.0)/2000)*(brd.y-tld.y)+tld.y;

            Point2D.Double newCenter = tgLensSet.convDrawToReal(newx,newy);
            offset.setLocation( offset.x+(newCenter.x-center.x),
                                offset.y+(newCenter.y-center.y) );
            tgPanel.processGraphMove();
        }
    }

    private class horizAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
              if(!adjustmentIsInternal) {
                  adjustHOffset();
                tgPanel.repaintAfterMove();
            }
        }
    }

    private class vertAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if(!adjustmentIsInternal) {
                adjustVOffset();
                tgPanel.repaintAfterMove();
            }
        }
    }


     class HVLens extends TGAbstractLens {
        protected void applyLens(Point2D.Double p) {
            p.x=p.x-offset.x;
            p.y=p.y-offset.y;
        }

        protected void undoLens(Point2D.Double p) {
            p.x=p.x+offset.x;
            p.y=p.y+offset.y;
        }
     }

     public void setOffset(Point p) {
        offset.setLocation(p.x,p.y);
        tgPanel.processGraphMove(); //Adjust draw coordinates to include new offset
        graphMoved(); //adjusts scrollbars to fit draw coordinates
     }

     public Point getOffset() {
        return new Point((int) offset.x,(int) offset.y);
     }

     public void scrollAtoB(Point2D.Double drawFrom, Point2D.Double drawTo) {
        Point2D.Double from = tgLensSet.convDrawToReal(drawFrom);
        Point2D.Double to = tgLensSet.convDrawToReal(drawTo);
        offset.setLocation( offset.x+(from.x-to.x),
                            offset.y+(from.y-to.y) );
     }

     Thread scrollThread;

     // altheim: 2002-08-08 made public
     public void slowScrollToCenter( final Node n ) {
         final Point2D.Double drawFrom =new Point2D.Double(n.drawx,n.drawy);
         final Point2D.Double drawTo = getDrawCenter();
         scrolling = true;
         if ( scrollThread != null && scrollThread.isAlive()) scrollThread.interrupt();
         scrollThread = new Thread(tgPanel.getThreadGroup(),"scroll") {
             public void run() {
                double nx=-999, ny=-999, cx, cy, distFromCenter;
                boolean keepScrolling = true;
                int stopScrollingAttempt = 0;
                int scrollSteps=0;
                while ( keepScrolling && scrollSteps++ < 250 ) {
                    nx = n.drawx;
                    ny = n.drawy;
                    cx = getDrawCenter().x;
                    cy = getDrawCenter().y;
                    distFromCenter = Math.sqrt((nx-cx)*(nx-cx)+(ny-cy)*(ny-cy));
                    double newx, newy;
                    if ( distFromCenter > 5 ) {
                        newx = cx + (nx-cx)*((distFromCenter-5)/distFromCenter);
                        newy = cy + (ny-cy)*((distFromCenter-5)/distFromCenter);
                    } else {
                        newx = cx;
                        newy = cy;
                    }
                    scrollAtoB(new Point2D.Double(nx,ny), new Point2D.Double(newx,newy));

                    if ( noRepaintThread == null || !noRepaintThread.isAlive() ) {
                        // only repaint if 40 milliseconds
                        // have not ellapsed since last repaint
                        tgPanel.repaintAfterMove();
                    } else {
                        tgPanel.processGraphMove(); // otherwise, register scroll internally
                    }
                    try {
                        Thread.currentThread().sleep(20);
                    } catch ( InterruptedException ex ) {
                        keepScrolling = false;
                    }
                    if ( distFromCenter < 3 ) {
                        try {
                            Thread.currentThread().sleep(200); //Wait a little to make sure
                        } catch ( InterruptedException ex ) {
                            keepScrolling=false;
                        }
                        nx = n.drawx;
                        ny = n.drawy;
                        cx = getDrawCenter().x;
                        cy = getDrawCenter().y;
                        distFromCenter = Math.sqrt((nx-cx)*(nx-cx)+(ny-cy)*(ny-cy));
                        if ( distFromCenter < 3 ) keepScrolling=false;
                    }
/*
                double fx= drawFrom.x;
                double fy= drawFrom.y;
                double tx= drawTo.x;
                double ty= drawTo.y;
                int scrollSteps = (int) Math.sqrt((fx-tx)*(fx-tx)+(fy-ty)*(fy-ty))/10 + 1;
                for ( int i=0; i < scrollSteps-1; i++ ) {
                  //double fromPcnt = (Math.cos(Math.PI*i/scrollSteps)+1)/2;
                  //double toPcnt = (Math.cos(Math.PI*(i+1)/scrollSteps)+1)/2;
                    double fromPcnt = ((double) scrollSteps-i)/scrollSteps;
                    double toPcnt = ((double) scrollSteps-1-i)/scrollSteps;
                    double midfx = fx*fromPcnt+tx*(1-fromPcnt);
                    double midfy = fy*fromPcnt+ty*(1-fromPcnt);
                    double midtx = fx*toPcnt+tx*(1-toPcnt);
                    double midty = fy*toPcnt+ty*(1-toPcnt);
                    scrollAtoB(new Point2D.Double(midfx,midfy), new Point2D.Double(midtx,midty));
                    tgPanel.repaintAfterMove();
                    tgPanel.sleepTGThread(50);
*/
                }
                scrollAtoB(new Point2D.Double(n.drawx,n.drawy),getDrawCenter()); //for good measure
                tgPanel.repaintAfterMove();
                HVScroll.this.scrolling = false;
            }
        };
        scrollThread.start();
    }


    class HVScrollToCenterUI extends TGAbstractClickUI {
        public void mouseClicked(MouseEvent e) {
            Node mouseOverN=tgPanel.getMouseOverN();
            if(!scrolling && mouseOverN!=null)
                slowScrollToCenter(mouseOverN);
        }
    }

    class HVDragUI extends TGAbstractDragUI{
        Point2D.Double lastMousePos;
        HVDragUI() { super(HVScroll.this.tgPanel); }

        public void preActivate() {}
        public void preDeactivate() {}

        public void mousePressed(MouseEvent e) {
            lastMousePos = new Point2D.Double(e.getX(), e.getY());
        }
        public void mouseReleased(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {
            if(!scrolling) scrollAtoB(lastMousePos, new Point2D.Double(e.getX(), e.getY()));
            lastMousePos.setLocation(e.getX(),e.getY());
            this.tgPanel.repaintAfterMove();
        }
    }

} // end com.touchgraph.graphlayout.interaction.HVScroll
