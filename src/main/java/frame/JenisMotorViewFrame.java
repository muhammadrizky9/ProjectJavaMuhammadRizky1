package frame;

import helpers.JasperDataSourceBuilder;
import helpers.koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class JenisMotorViewFrame extends JFrame{
    private JPanel panel1;
    private JPanel mainPanel;
    private JPanel cariPanel;
    private JTextField cariTextField;
    private JButton cariButton;
    private JScrollPane viewScrollPanel;
    private JTable viewTable;
    private JPanel buttonPanel;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JButton tutupButton;

    public JenisMotorViewFrame(){
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTable();
        });
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e){
                isiTable();
            }
        });
        tambahButton.addActionListener(e -> {
            JenisMotorInputFrame inputFrame = new JenisMotorInputFrame();
            inputFrame.setVisible(true);
        });
        cariButton.addActionListener(e -> {
            if (cariTextField.getText().equals("")){
                JOptionPane.showMessageDialog(null,"Isi kata kunci pencarian","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                cariTextField.requestFocus();
                return;
            }
            Connection c = koneksi.getConnection();
            String keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "SELECT K.*,B.nama AS nama_brand FROM jenismotor K LEFT JOIN brandmotor B ON K.brand_id = B.id WHERE K.nama like ? OR B.nama like ?";
            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1,keyword);
                ps.setString(2,keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[6];
                while (rs.next()){
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nama");
                    row[2] = rs.getString("nama_brand");
                    row[3] = rs.getString("klasifikasi");
                    row[4] = rs.getInt("silinder");
                    row[5] = rs.getDouble("diameter");
                    dtm.addRow(row);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        ubahButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            TableModel tm = viewTable.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
            JenisMotorInputFrame inputFrame = new JenisMotorInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });
        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null,"Yakin mau dihapus ?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION);
            if (pilihan ==0){
                TableModel tm = viewTable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
                Connection c = koneksi.getConnection();
                String deleteSQL = "delete from jenismotor where id = ?";
                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1,id);
                    ps.executeUpdate();
                } catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });
        
        isiTable();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Data Jenis Motor");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTable(){
        Connection c = koneksi.getConnection();
        String selectSQL = "SELECT K.*,B.nama AS nama_brand FROM jenismotor K LEFT JOIN brandmotor B ON K.brand_id = B.id";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            String header[] = {"id","Nama Jenis Motor","Nama Brand","Klasifikasi","Populasi","Diameter Piston"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            viewTable.setModel(dtm);
            viewTable.getColumnModel().getColumn(0).setMaxWidth(32);
            viewTable.getColumnModel().getColumn(1).setMinWidth(150);
            viewTable.getColumnModel().getColumn(2).setMinWidth(150);

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            viewTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            viewTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

            Object[] row = new Object[6];
            while (rs.next()){

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                String rowSilinder = nf.format(rs.getInt("silinder"));
                String rowLuas = String.format("%,.2f", rs.getDouble("diameter"));

                row[0] = rs.getInt("id");
                row[1] = rs.getString("nama");
                row[2] = rs.getString("nama_brand");
                row[3] = rs.getString("klasifikasi");
                row[4] = rowSilinder;
                row[5] = rowLuas;
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
