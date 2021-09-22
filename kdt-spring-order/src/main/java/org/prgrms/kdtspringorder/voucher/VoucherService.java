package org.prgrms.kdtspringorder.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(@Qualifier("memory") VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

//    public void setVoucherRepository(VoucherRepository voucherRepository){
//        this.voucherRepository = voucherRepository;
//    }

    private final List<Voucher> vouchers = new ArrayList<>();

    public Voucher getVoucher(UUID voucherId) {
        return voucherRepository
                .findById(voucherId)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Can not find a voucher for {0}", voucherId)));

    }

//    public Optional<Voucher> createVoucher(VoucherType voucherType) {
//        UUID voucherId = UUID.randomUUID();
//        Optional<Voucher> voucher;
//        if (voucherType.equals(VoucherType.FixedAmountVoucher)) {
//            voucher = Optional.of(new FixedAmountVoucher(voucherId, 1000));
//        } else if (voucherType.equals(VoucherType.PercentDiscountVoucher)) {
//            voucher = Optional.of(new PercentDiscountVoucher(voucherId, 10));
//        } else {
//            voucher = Optional.empty();
//        }
//        return voucher;
//    }
//    public void addVoucher(Optional<Voucher> voucher){
//        voucherRepository.insert(voucher);
//    }
//
//    public List<Voucher> getAllVouchers(){
//        return voucherRepository.getAllVoucher();
//    }
    public void useVoucher(Voucher voucher) {
    }
}