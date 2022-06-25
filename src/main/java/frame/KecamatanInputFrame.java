package frame;

import helpers.ComboBoxItem;
import helpers.koneksi;

import javax.swing.*;
import java.sql.*;

public class KecamatanInputFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField idTextField;
    private JTextField namaTextField;
    private JPanel buttonPanel;
    private JButton simpanButton;
    private JButton batalButton;
    private JComboBox kabupatenComboBox;

    private int id;

    public void setId(int id){
        this.id = id;
    }

    public KecamatanInputFrame(){
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
            ComboBoxItem item = (ComboBoxItem) kabupatenComboBox.getSelectedItem();
            int kabupatenId = item.getValue();
            if (kabupatenId == 0){
                JOptionPane.showMessageDialog(null,"Pilih kabupaten","Validasi ComboBox",JOptionPane.WARNING_MESSAGE);
                kabupatenComboBox.requestFocus();
                return;
            }
            Connection c = koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM kecamatan WHERE nama= ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO kecamatan (id,nama,kabupaten_id) VALUES (NULL, ?, ?)";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1,nama);
                        ps.executeUpdate();
                        dispose();
                    }

                } else {
                    String cekSQL = "SELECT * FROM kecamatan WHERE nama= ? AND id != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ps.setInt(2,id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String updateSQL = "UPDATE kecamatan SET nama= ?, kabupaten= ? WHERE id= ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,kabupatenId);
                        ps.setInt(3,id);
                        ps.executeUpdate();
                        dispose();
                    }

                }

            } catch (SQLException ex) {
                throw  new RuntimeException(ex);
            }
        });
        kustomisasiKomponen();
        init();
    }
    public void isiKomponen(){
        Connection c = koneksi.getConnection();
        String findSQL = "SELECT * FROM kecamatan WHERE id= ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaTextField.setText(rs.getString("nama"));
                int kabupatenId = rs.getInt("kabupaten_id");
                for (int i = 0; i < kabupatenComboBox.getItemCount(); i++){
                    kabupatenComboBox.setSelectedIndex(i);
                    ComboBoxItem item = (ComboBoxItem) kabupatenComboBox.getSelectedItem();
                    if (kabupatenId == item.getValue()){
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Kecamatan");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void kustomisasiKomponen(){
        Connection c = koneksi.getConnection();
        String selectSQL = "SELECT * FROM kabupaten ORDER BY nama";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            kabupatenComboBox.addItem(new ComboBoxItem(0,"Pilih Kabupaten"));
            while (rs.next()){
                kabupatenComboBox.addItem(new ComboBoxItem(rs.getInt("id"),rs.getString("nama")));
            }
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    }

//    public void kustomisasiKomponen(){
//        Connection c = koneksi.getConnection();
//        String selectSQL = "SELECT * FROM kabupaten ORDER BY nama";
//        try {
//            Statement s = c.createStatement();
//            ResultSet rs = s.executeQuery(selectSQL);
//            kabupatenComboBox.addItem(new ComboBoxItem(0,"Pilih Kabupaten"));
//            while (rs.next()){
//                kabupatenComboBox.addItem(new ComboBoxItem(rs.getInt("id"),rs.getString("nama")));
//            }
//        } catch (SQLException ex){
//            throw new RuntimeException(ex);
//        }
//    }
//}
