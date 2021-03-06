package frame;

import helpers.koneksi;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BrandInputFrame extends JFrame{
    private JPanel panel1;
    private JPanel mainPanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JPanel buttonPanel;
    private JButton simpanButton;
    private JButton batalButton;

    private int id;

    public void setId(int id){
        this.id = id;
    }

    public BrandInputFrame(){
        batalButton.addActionListener(e -> {
            dispose();
        });
        simpanButton.addActionListener(e ->{
            String nama = namaTextField.getText();
            if (nama.equals("")){
                JOptionPane.showMessageDialog(null,"Isi kata kunci pencarian","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                namaTextField.requestFocus();
                return;
            }
            Connection c = koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM brandmotor WHERE nama= ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO brandmotor VALUES (NULL, ?)";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1,nama);
                        ps.executeUpdate();
                        dispose();
                    }

                } else {
                    String cekSQL = "SELECT * FROM brandmotor WHERE nama= ? AND id != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ps.setInt(2,id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String updateSQL = "UPDATE brandmotor SET nama= ? WHERE id= ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,id);
                        ps.executeUpdate();
                        dispose();
                    }

                }

            } catch (SQLException ex) {
                throw  new RuntimeException(ex);
            }
        });
        init();
    }

    public void isiKomponen(){
        Connection c = koneksi.getConnection();
        String findSQL = "SELECT * FROM brandmotor WHERE id= ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Brand Motor");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
