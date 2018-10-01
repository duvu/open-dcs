package com.vd5.dcs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author beou on 7/28/18 11:02
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellTower implements Serializable {

    private static final long serialVersionUID = 2732961359942346620L;
    int mcc;
    int mnc;
    int lac;
    int cid;
    int rssi;
}
