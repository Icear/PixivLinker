package com.github.icear;

class PMember implements PixivMember {

    private int id;
    private String name;
    private byte[] image;

    PMember(int id, String name, byte[] image){
        this.id = id;
        this.name = name;
        this.image = image;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getImage() {
        return image;
    }

//    @Override
//    public PMember[] getFollowedList() {
//        return new PMember[0];
//    }
//
//    @Override
//    public PWork[] getCollectionList() {
//        return new PWork[0];
//    }
//
//    @Override
//    public PWork[] getWorkList() {
//        return new PWork[0];
//    }
}
