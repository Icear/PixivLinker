package com.github.icear;

class PMember implements PixivMember {

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public byte[] getImage() {
        return new byte[0];
    }

    @Override
    public PMember[] getConcernedList() {
        return new PMember[0];
    }

    @Override
    public PMember[] getFollowedList() {
        return new PMember[0];
    }

    @Override
    public PixivWork[] getCollectionList() {
        return new PixivWork[0];
    }

    @Override
    public PixivWork[] getWorkList() {
        return new PixivWork[0];
    }
}
