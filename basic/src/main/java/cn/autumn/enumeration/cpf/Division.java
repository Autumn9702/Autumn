package cn.autumn.enumeration.cpf;

/**
 * @author cf
 * Created in 14:53 2022/10/8
 */
public enum Division {

    /**
     * Division define
     */

    SX_DISTRICT("市辖区"),
    COUNTY("县"),
    SX_COUNTY("市辖县"),
    SXN_CITY("省属虚拟市"),
    S_ADMINISTRATIVE_UNITS("省直辖行政单位")
    ;

    private final String vul;

    Division(String vul) {
        this.vul = vul;
    }

    public String getVul() {
        return this.vul;
    }
}
