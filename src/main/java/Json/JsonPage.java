package Json;

public class JsonPage {
    private int page = 1;
    private int per_page = 1;
    private int total = 10;

    public JsonPage() {
    }

    public void incPage() {
        page++;
    }

    public boolean finished() {
        return this.per_page * this.page > this.total + this.per_page;
    }

    public String urlPageParam() {
        return "&page=" + this.page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


}
