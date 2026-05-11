/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package myapplication;

import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author JRK PISONET
 */
public class login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(login.class.getName());

   public login() {
    initComponents(); // This must stay at the top

    
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pwdtxt = new javax.swing.JPasswordField();
        unametxt = new javax.swing.JTextField();
        Login = new javax.swing.JButton();
        CREATEACCOUNT = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        features = new javax.swing.JToggleButton();
        About = new javax.swing.JToggleButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        Showpass = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("PASSWORD:");
        jLabel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 350, 130, 40));

        jLabel3.setBackground(new java.awt.Color(102, 102, 102));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("USERNAME:");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 250, 130, 40));

        pwdtxt.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pwdtxt.addActionListener(this::pwdtxtActionPerformed);
        getContentPane().add(pwdtxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 350, 210, 40));

        unametxt.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        unametxt.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        unametxt.addActionListener(this::unametxtActionPerformed);
        getContentPane().add(unametxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 250, 210, 40));

        Login.setBackground(new java.awt.Color(153, 153, 153));
        Login.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Login.setText("LOGIN");
        Login.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Login.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Login.addActionListener(this::LoginActionPerformed);
        getContentPane().add(Login, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 410, 150, 50));

        CREATEACCOUNT.setBackground(new java.awt.Color(153, 153, 153));
        CREATEACCOUNT.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        CREATEACCOUNT.setText("CREATE ACCOUNT");
        CREATEACCOUNT.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CREATEACCOUNT.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CREATEACCOUNT.addActionListener(this::CREATEACCOUNTActionPerformed);
        getContentPane().add(CREATEACCOUNT, new org.netbeans.lib.awtextra.AbsoluteConstraints(1450, 490, 210, 40));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("PLEASE ENTER USERNAME AND PASSWORD");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 170, 430, 40));

        features.setBackground(new java.awt.Color(153, 153, 153));
        features.setText("FEATURES");
        features.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        features.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        features.addActionListener(this::featuresActionPerformed);
        getContentPane().add(features, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 170, 30));

        About.setBackground(new java.awt.Color(153, 153, 153));
        About.setText("ABOUT");
        About.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        About.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        About.setOpaque(true);
        About.addActionListener(this::AboutActionPerformed);
        getContentPane().add(About, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 150, 30));
        getContentPane().add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        Showpass.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Showpass.addActionListener(this::ShowpassActionPerformed);
        getContentPane().add(Showpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(1680, 360, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setIcon(new javax.swing.ImageIcon("C:\\Users\\JRK PISONET\\Documents\\NetBeansProjects\\Myapplication\\src\\myapplication\\agif1opt.gif")); // NOI18N
        jLabel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 130, 550, 470));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon("C:\\Users\\JRK PISONET\\Downloads\\68b2c790e0f92b9f62fb870aa0ac7d56.gif")); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 490, 90));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon("C:\\Users\\JRK PISONET\\Downloads\\994024.png")); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1890, 860));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pwdtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pwdtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pwdtxtActionPerformed

    private void LoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginActionPerformed
          // TODO add your handling code here:
                                             
                                       
                                         
    String username = unametxt.getText().trim();
    String password = new String(pwdtxt.getPassword()).trim();

    try {
       java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.txt"));

        String line;
        boolean found = false;
        String firstName = "", lastName = "";

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                String storedFirst = parts[0];
                String storedLast = parts[1];
                String storedUser = parts[2];
                String storedPass = parts[3];

                if (storedUser.equals(username) && storedPass.equals(password)) {
                    found = true;
                    firstName = storedFirst;
                    lastName = storedLast;
                    break;
                }
            }
        }
        br.close();

      if (found) {
    javax.swing.JOptionPane.showMessageDialog(this, "Login Successful!");
    new Mainframe(firstName, lastName).setVisible(true); // ✅ correct
    this.dispose();


        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error reading accounts: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }




    }//GEN-LAST:event_LoginActionPerformed

    private void unametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unametxtActionPerformed

    private void CREATEACCOUNTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CREATEACCOUNTActionPerformed
        // TODO add your handling code here:
                                                    
    // Open the Createaccount form
    new Createaccount().setVisible(true);
    // Close the login form if you want
    this.dispose();


    }//GEN-LAST:event_CREATEACCOUNTActionPerformed

    private void featuresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_featuresActionPerformed
        // TODO add your handling code here:
         SwingUtilities.invokeLater(() -> {
        try {
            features main = new features();
            main.setVisible(true);
            main.toFront();
            main.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
    }//GEN-LAST:event_featuresActionPerformed

    private void AboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutActionPerformed
        // TODO add your handling code here:
        SwingUtilities.invokeLater(() -> {
        try {
            about main = new about();
            main.setVisible(true);
            main.toFront();
            main.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
    }//GEN-LAST:event_AboutActionPerformed

    private void ShowpassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowpassActionPerformed
        // TODO add your handling code here:
      if (Showpass.isSelected()) {
        pwdtxt.setEchoChar((char) 0); // Show password
    } else {
        pwdtxt.setEchoChar('*');      // Hide password
    }

    }//GEN-LAST:event_ShowpassActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]){
       


   
        java.awt.EventQueue.invokeLater(() -> new login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton About;
    private javax.swing.JButton CREATEACCOUNT;
    private javax.swing.JButton Login;
    private javax.swing.JCheckBox Showpass;
    private javax.swing.JToggleButton features;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField pwdtxt;
    private javax.swing.JTextField unametxt;
    // End of variables declaration//GEN-END:variables

    private static class Exeption extends Exception {

        public Exeption() {
        }
    }

    private static class VideoBackground {

        public VideoBackground() {
        }

        private void setVisible(boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
