package com.sikoramarek.gameOfLife.view;

import com.sikoramarek.gameOfLife.model.Dot;

public interface ViewInterface {

	public void refresh(Dot[][] board);

	void setMulti(boolean multi);
}
