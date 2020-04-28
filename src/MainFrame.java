import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private DetailsPanel detailsPanel;

public MainFrame(String title)
{
    super(title);

    setLayout(new BorderLayout());

    JTextArea textArea = new JTextArea();
    JScrollPane sb = new JScrollPane(textArea);
    JButton button = new JButton("but1");

    detailsPanel = new DetailsPanel();
    JScrollPane dp = new JScrollPane(detailsPanel);

    detailsPanel.addDetailListener(new DetailListener(){

        public void detailEventOccured(DetailEvent e )
        {
            String text = e.getText();
            textArea.setText(text);
        }


    });



    Container c = getContentPane();
    c.add(sb,BorderLayout.CENTER);
    c.add(button,BorderLayout.SOUTH);
    c.add(dp,BorderLayout.WEST);


    button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {


            System.out.println(textArea.getText());

        }
    });



}






}




















