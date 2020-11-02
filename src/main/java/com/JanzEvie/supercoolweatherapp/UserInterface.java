package com.JanzEvie.supercoolweatherapp;//package com.JanzEvie.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.WordUtils;


public class UserInterface extends JPanel
{
    /*********************************************************
     *					Member variables					 *
     *********************************************************/
    static JFrame frame = null;
    static JPanel mainPanel = null;
    static UserInterface displayPanel = null;
    static JLabel heading = null;
    static JTextField address = null;
    static JButton currentTemp = null;
    static JButton forecastToday = null;
    static JButton sevenDayForecast = null;
    static Forecast[] forecast = null;
    static Forecast now = null;
    static String location = null;
    static String fullLocation = null;
    static boolean showCurrentTemp = false;
    static boolean showForecastToday = false;
    static boolean showSevenDayForecast = false;

    /*********************************************************
     * 							main						 *
     *********************************************************/
    public static void main( String[] args ) throws InterruptedException
    {
        //Initialize all of the objects for the java applet
        frame = new JFrame();
        mainPanel = new JPanel();
        displayPanel = new UserInterface();
        heading = new JLabel();
        address = new JTextField( "Search address, location, zip..." , 50 );
        currentTemp = new JButton( "Current Temp" );
        forecastToday = new JButton( "Today's Forecast" );
        sevenDayForecast = new JButton( "Seven Day Forecast" );

        //Set up the applet
        setUpMainPanel();
        setUpDisplayPanel();
        setUpHeading();
        setUpFrame();
        setUpTextField(); //also sets up functionality of text field
        setUpButtons(); //also sets up functionality of buttons

        //Make the window visible
        frame.setVisible( true );

    }//main

    /*********************************************************
     *				   addButtonFunctionality	    		 *
     *********************************************************/
    static public void addButtonFunctionality()
    {
        //"Current Temp" button
        currentTemp.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showCurrentTemp = true;
                showForecastToday = false;
                showSevenDayForecast = false;
                displayPanel.repaint();
            }
        });

        //"Today's Forecast" button
        forecastToday.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCurrentTemp = false;
                showForecastToday = true;
                showSevenDayForecast = false;
                displayPanel.repaint();
            }
        });

        //"Seven Day Forecast" button
        sevenDayForecast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCurrentTemp = false;
                showForecastToday = false;
                showSevenDayForecast = true;
                displayPanel.repaint();
            }
        });

    }//addButtonFunctionality

    /*********************************************************
     *				   addTextFieldFunctionality	   		 *
     *********************************************************/
    static public void addTextFieldFunctionality()
    {
        address.addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e )
            {
                //address = null;
                location = null;
                address.setBackground( Color.WHITE );
            }

            public void focusLost( FocusEvent e ) {
                getAddress( address );
            }

        });

    }//addTextFieldFunctionality

    /*********************************************************
     *						getAddress						 *
     *********************************************************/
    static public void getAddress( JTextField address )
    {
        boolean valid = false;

        //Obtain a valid address from the user
        while( !valid )
        {
            //valid = false;

            try {
                location = address.getText();
                location = location.replaceAll(" ", "+");
                forecast = NwsParser.getSevenDayForecast( location );
                now = NwsParser.getCurrentWeather( location );
                fullLocation = GeoCoords.getFullLocation( location );
                address.setBackground( new Color( 229, 255, 204) );
            }

            catch ( RuntimeException e ) {
                address.setBackground( new Color( 255, 204, 204) );
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.INFORMATION_MESSAGE );
                location = null;
                displayPanel.repaint();
            }

            valid = true;

        }//while

    }//getAddress

    /*********************************************************
     *							paint						 *
     *********************************************************/
    @Override
    public void paint( Graphics g )
    {
        //Call the constructor for the original method
        super.paint( g );

        //Display desired weather information on display panel
        if (showCurrentTemp && location != null ) { paintCurrentTemp(g); }
        else if (showForecastToday && location != null) { paintForecastToday(g); }
        else if (showSevenDayForecast && location != null) {
            try { paintSevenDayForecast(g); }
            catch (IOException e) {}
        }

    }//paint

    /*********************************************************
     *					   paintCurrentTemp		    		 *
     *********************************************************/
    public void paintCurrentTemp( Graphics g )
    {
        try
        {
            g.drawImage( ImageIO.read( new URL( forecast[ 0 ].icon ) ),100,100, 200, 200, null );
        }
        catch ( MalformedURLException e ) {}
        catch ( IOException e ) {}

        g.drawString( fullLocation, 5, 390);
        g.setFont(new Font( "Century", Font.PLAIN, 30));

        if( now.shortForecast.length() > 14 )
        {
            StringBuilder str = new StringBuilder( now.shortForecast );
            String[] splitStr = stringPrep( str, 14 );
            g.drawString( splitStr[ 0 ], 325, 220 );
            g.drawString( splitStr[ 1 ], 325, 255 );
        }
        else
        {
            g.drawString( now.shortForecast, 325, 220 );
        }

        g.setFont(new Font( "Century", Font.BOLD, 30));
        g.drawString( "Temp" + now.toString(), 325, 175);

    }//paintCurrentTemp

    /*********************************************************
     *				   paintForecastToday		    		 *
     *********************************************************/
    public void paintForecastToday( Graphics g )
    {
        try
        {
            g.drawImage( ImageIO.read( new URL( forecast[ 0 ].icon ) ),100,35, 150, 150, null );
            g.drawImage(ImageIO.read( new URL( forecast[ 1 ].icon ) ),100,210,150, 150, null );
        }
        catch ( MalformedURLException e ) {}
        catch (IOException e) {}

        g.drawString( fullLocation, 5, 390);

        g.setFont( new Font( "Century", Font.PLAIN, 20 ) );

        //Handle the first forecast
        if( forecast[ 0 ].shortForecast.length() > 20 )
        {
            StringBuilder str = new StringBuilder( forecast[ 0 ].shortForecast );
            String[] splitStr = stringPrep( str, 20 );
            g.drawString( splitStr[ 0 ], 275, 140 );
            g.drawString( splitStr[ 1 ], 275, 165 );
        }
        else
        {
            g.drawString( forecast[ 0 ].shortForecast, 275,140 );
        }

        //Handle the second forecast
        if( forecast[ 1 ].shortForecast.length() > 20 )
        {
            StringBuilder str = new StringBuilder( forecast[ 0 ].shortForecast );
            String[] splitStr = stringPrep( str, 20 );
            g.drawString( splitStr[ 0 ], 275, 310 );
            g.drawString( splitStr[ 1 ], 275, 335 );
        }
        else
        {
            g.drawString( forecast[ 1 ].shortForecast, 275,310 );
        }

        g.setFont( new Font( "Century", Font.BOLD, 20 ) );
        g.drawString( forecast[ 0 ].toString(), 275,110 );
        g.drawString( forecast[ 1 ].toString(), 275,280 );

    }//paintForecastToday

    /*********************************************************
     *				   paintSevenDayForecast	    		 *
     *********************************************************/
    public void paintSevenDayForecast( Graphics g ) throws IOException, MalformedURLException
    {
        //Make sure 7 day forecast shows daytime temps
        int i = -1;
        if( forecast[ 0 ].isDayTime ) { i = 0; }
        else { i = 1; }

        //Draw each image
        try
        {
            g.drawImage( ImageIO.read( new URL( forecast[ i ].icon ) ),25,95, 150, 150, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 2 ].icon ) ),200,55, 100, 100, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 4 ].icon ) ),325,55, 100, 100, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 6 ].icon ) ),450,55, 100, 100, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 8 ].icon ) ),200,220, 100, 100, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 10 ].icon ) ),325,220, 100, 100, null );
            g.drawImage( ImageIO.read( new URL( forecast[ i + 12 ].icon ) ),450,220, 100, 100, null );
        }
        catch ( MalformedURLException e ) {}
        catch ( IOException e ) {}

        g.drawString( fullLocation, 5, 390);

        g.setFont( new Font( "Century", Font.PLAIN, 13 ) );
        //Handle the first forecast
        if( forecast[ 0 ].shortForecast.length() > 20 )
        {
            StringBuilder str = new StringBuilder( forecast[ 0 ].shortForecast );
            String[] splitStr = stringPrep( str, 20 );
            g.drawString( splitStr[ 0 ], 30, 290 );
            g.drawString( splitStr[ 1 ], 30, 305 );
        }
        else
        {
            g.drawString( forecast[ i ].shortForecast, 30,290);
        }

        g.setFont( new Font( "Century", Font.BOLD, 13 ) );
        g.drawString( forecast[ i ].toString(), 30,270);

        g.setFont( new Font( "Century", Font.BOLD, 11 ) );
        g.drawString( forecast[ i + 2 ].toString(), 205,175);
        g.drawString( forecast[ i + 4 ].toString(), 330,175);
        g.drawString( forecast[ i + 6 ].toString(), 455,175);
        g.drawString( forecast[ i + 8 ].toString(), 205,340);
        g.drawString( forecast[ i + 10 ].toString(), 330,340);
        g.drawString( forecast[ i + 12 ].toString(), 455,340);

    }//paintSevenDayForecast

    /*********************************************************
     *					    setUpButtons		    		 *
     *********************************************************/
    static public void setUpButtons()
    {
        currentTemp.setBounds( 150, 200, 300, 50 );
        forecastToday.setBounds( 500, 200, 300, 50 );
        sevenDayForecast.setBounds( 850, 200, 300, 50 );
        addButtonFunctionality();
        mainPanel.add( currentTemp );
        mainPanel.add( forecastToday );
        mainPanel.add( sevenDayForecast );

    }//setUpButtons

    /*********************************************************
     *					    setUpDisplayPanel				 *
     *********************************************************/
    static public void setUpDisplayPanel()
    {
        mainPanel.add( displayPanel );
        displayPanel.setBounds( 350, 300, 580, 400 );
        displayPanel.setBackground( Color.WHITE );
        displayPanel.setLayout( null );
        displayPanel.setLayout( new BorderLayout() );
        displayPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK, 3 ) );

    }//setUpDisplayPanel

    /*********************************************************
     *					    setUpFrame		    			 *
     *********************************************************/
    static public void setUpFrame()
    {
        frame.setContentPane( mainPanel );
        frame.setSize( 1280, 900 );
        frame.setTitle( "Super Cool Weather App" );
        frame.getContentPane().setLayout( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    }//setUpFrame

    /*********************************************************
     *					    setUpHeading		   			 *
     *********************************************************/
    static public void setUpHeading()
    {
        //Set the heading's font
        Font font = new Font("Century", Font.BOLD,20);

        //Add the heading
        heading.setText( "Super Cool Weather App" );
        heading.setFont( font );
        heading.setBounds( 520, 50, 500, 25 );
        mainPanel.add( heading );

    }//setUpHeading

    /*********************************************************
     *					    setUpMainPanel					 *
     *********************************************************/
    static public void setUpMainPanel()
    {
        mainPanel.setSize(1280, 1024);
        mainPanel.setBackground( Color.WHITE );
        mainPanel.setLayout( null );
        mainPanel.setLayout( new BorderLayout() );

    }//setUpMainPanel

    /*********************************************************
     *					    setUpTextField	    			 *
     *********************************************************/
    static public void setUpTextField()
    {
        address.setBounds( 400, 100, 500, 50 );
        addTextFieldFunctionality();
        mainPanel.add( address );

    }//setUpTextField

    /*********************************************************
     *					    stringPrep		        		 *
     *********************************************************/
    private String[] stringPrep( StringBuilder str, int maxLen )
    {
        String[] splitStr = null;

        if( str.length() > maxLen ) {
            for (int i = maxLen; i > 0; i--) {
                if (str.charAt(i) == ' ') {
                    str.setCharAt(i, '\n');
                    break;
                }
            }

            splitStr = str.toString().split("\n", 2);
        }

        return splitStr;

    }//stringPrep

}//com.JanzEvie.supercoolweatherapp.UserInterface
